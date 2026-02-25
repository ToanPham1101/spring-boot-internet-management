package internet.management.service;

import internet.management.entity.CartItemEntity;
import internet.management.entity.ItemEntity;
import internet.management.entity.OrderEntity;
import internet.management.entity.OrderItemEntity;
import internet.management.entity.UserBalanceTransactionEntity;
import internet.management.entity.UserEntity;
import internet.management.model.CreateOrderCommand;
import internet.management.model.ItemType;
import internet.management.model.OrderStatus;
import internet.management.model.SearchOrdersQuery;
import internet.management.model.SearchOrdersResult;
import internet.management.model.TransactionType;
import internet.management.repository.CartItemRepository;
import internet.management.repository.OrderItemRepository;
import internet.management.repository.OrderRepository;
import internet.management.repository.UserBalanceTransactionRepository;
import internet.management.repository.UserRepository;
import internet.management.repository.service.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserBalanceTransactionRepository transactionRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository,
                        ItemRepository itemRepository,
                        UserRepository userRepository,
                        UserBalanceTransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public SearchOrdersResult searchOrders(SearchOrdersQuery query) {
        Integer orderStatusValue = query.getOrderStatus() != null ? query.getOrderStatus().getValue() : null;
        List<OrderEntity> orders = orderRepository.searchOrders(query.getUserId(), orderStatusValue);

        List<Integer> orderIds = orders.stream().map(OrderEntity::getId).collect(Collectors.toList());

        List<OrderItemEntity> allOrderItems = orderIds.isEmpty()
                ? new ArrayList<>()
                : orderItemRepository.findByOrderIdIn(orderIds);

        Map<Integer, List<OrderItemEntity>> orderItemsMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItemEntity::getOrderId));

        List<Integer> itemIds = allOrderItems.stream()
                .map(OrderItemEntity::getItemId).distinct().collect(Collectors.toList());

        Map<Integer, ItemEntity> itemMap = itemIds.isEmpty() ? Map.of()
                : itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, i -> i));

        List<Integer> userIds = orders.stream()
                .map(OrderEntity::getUserId).distinct().collect(Collectors.toList());

        Map<Integer, UserEntity> userMap = userIds.isEmpty() ? Map.of()
                : userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        SearchOrdersResult result = new SearchOrdersResult();
        result.setOrders(orders.stream().map(order -> {
            SearchOrdersResult.Order dto = new SearchOrdersResult.Order();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUserId());
            dto.setOrderStatus(fromValue(order.getOrderStatus()));
            dto.setDiscount(order.getDiscount());

            UserEntity user = userMap.get(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }

            List<OrderItemEntity> orderItems = orderItemsMap.getOrDefault(order.getId(), new ArrayList<>());
            dto.setItems(orderItems.stream().map(oi -> {
                SearchOrdersResult.OrderItem itemDto = new SearchOrdersResult.OrderItem();
                itemDto.setItemId(oi.getItemId());
                itemDto.setQuantity(oi.getQuantity());
                itemDto.setPrice(oi.getPrice());
                ItemEntity ie = itemMap.get(oi.getItemId());
                if (ie != null) {
                    itemDto.setItemName(ie.getName());
                    itemDto.setItemType(ie.getItemType() != null ? ItemType.nameOf(ie.getItemType()) : null);
                }
                return itemDto;
            }).collect(Collectors.toList()));

            int total = orderItems.stream().mapToInt(oi -> oi.getQuantity() * oi.getPrice()).sum();
            dto.setTotalAmount(total - order.getDiscount());

            return dto;
        }).collect(Collectors.toList()));

        return result;
    }

    @Transactional
    public void createOrder(CreateOrderCommand command) {
        UserEntity user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + command.getUserId()));

        List<CartItemEntity> cartItems = cartItemRepository.findByUserId(command.getUserId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + command.getUserId());
        }

        List<Integer> itemIds = cartItems.stream().map(CartItemEntity::getItemId).collect(Collectors.toList());
        Map<Integer, ItemEntity> itemMap = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, i -> i));

        int discount = command.getDiscount() != null ? command.getDiscount() : 0;
        int totalBeforeDiscount = 0;
        for (CartItemEntity ci : cartItems) {
            ItemEntity item = itemMap.get(ci.getItemId());
            if (item != null) {
                totalBeforeDiscount += ci.getQuantity() * item.getPrice();
            }
        }
        int totalAmount = Math.max(0, totalBeforeDiscount - discount);

        if (user.getBalance() < totalAmount) {
            throw new RuntimeException("Insufficient balance. Required: " + totalAmount
                    + " VND, Available: " + user.getBalance() + " VND");
        }

        OrderEntity order = new OrderEntity();
        order.setUserId(command.getUserId());
        order.setDiscount(discount);
        order.setOrderStatus(OrderStatus.NEW.getValue());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        for (CartItemEntity ci : cartItems) {
            OrderItemEntity oi = new OrderItemEntity();
            oi.setOrderId(order.getId());
            oi.setItemId(ci.getItemId());
            oi.setQuantity(ci.getQuantity());
            ItemEntity item = itemMap.get(ci.getItemId());
            oi.setPrice(item != null ? item.getPrice() : 0);
            orderItemRepository.save(oi);
        }

        user.setBalance(user.getBalance() - totalAmount);
        userRepository.save(user);

        UserBalanceTransactionEntity tx = new UserBalanceTransactionEntity();
        tx.setUserId(user.getId());
        tx.setAmount(-totalAmount);
        tx.setType(TransactionType.ORDER_PAYMENT.getValue());
        tx.setDescription("Order #" + order.getId() + " - " + cartItems.size() + " items");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        cartItemRepository.deleteByUserId(command.getUserId());
    }

    private OrderStatus fromValue(Integer value) {
        if (value == null) return null;
        for (OrderStatus s : OrderStatus.values()) {
            if (s.getValue() == value) return s;
        }
        return null;
    }
}

