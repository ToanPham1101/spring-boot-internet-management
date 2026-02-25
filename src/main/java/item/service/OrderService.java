package item.service;

import item.entity.CartItemEntity;
import item.entity.ItemEntity;
import item.entity.OrderEntity;
import item.entity.OrderItemEntity;
import item.model.CreateOrderCommand;
import item.model.OrderStatus;
import item.model.SearchOrdersQuery;
import item.model.SearchOrdersResult;
import item.repository.CartItemRepository;
import item.repository.OrderItemRepository;
import item.repository.OrderRepository;
import item.repository.service.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository,
                        ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
    }

    public SearchOrdersResult searchOrders(SearchOrdersQuery query) {
        Integer orderStatusValue = query.getOrderStatus() != null ? query.getOrderStatus().getValue() : null;

        List<OrderEntity> orders = orderRepository.searchOrders(query.getUserId(), orderStatusValue);

        // Get all order ids
        List<Integer> orderIds = orders.stream()
                .map(OrderEntity::getId)
                .collect(Collectors.toList());

        // Fetch all order items for these orders
        List<OrderItemEntity> allOrderItems = orderIds.isEmpty()
                ? new ArrayList<>()
                : orderItemRepository.findByOrderIdIn(orderIds);

        // Group order items by order id
        Map<Integer, List<OrderItemEntity>> orderItemsMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItemEntity::getOrderId));

        // Get all item ids for names
        List<Integer> itemIds = allOrderItems.stream()
                .map(OrderItemEntity::getItemId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, ItemEntity> itemMap = itemIds.isEmpty()
                ? Map.of()
                : itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, item -> item));

        // Build result
        SearchOrdersResult result = new SearchOrdersResult();
        result.setOrders(orders.stream().map(order -> {
            SearchOrdersResult.Order orderDto = new SearchOrdersResult.Order();
            orderDto.setOrderId(order.getId());
            orderDto.setUserId(order.getUserId());
            orderDto.setOrderStatus(fromValue(order.getOrderStatus()));
            orderDto.setDiscount(order.getDiscount());

            List<OrderItemEntity> orderItems = orderItemsMap.getOrDefault(order.getId(), new ArrayList<>());

            List<SearchOrdersResult.OrderItem> itemDtos = orderItems.stream().map(oi -> {
                SearchOrdersResult.OrderItem itemDto = new SearchOrdersResult.OrderItem();
                itemDto.setItemId(oi.getItemId());
                itemDto.setQuantity(oi.getQuantity());
                itemDto.setPrice(oi.getPrice());

                ItemEntity itemEntity = itemMap.get(oi.getItemId());
                if (itemEntity != null) {
                    itemDto.setItemName(itemEntity.getName());
                }
                return itemDto;
            }).collect(Collectors.toList());

            orderDto.setItems(itemDtos);

            // totalAmount = sum(quantity * price) - discount
            int totalBeforeDiscount = orderItems.stream()
                    .mapToInt(oi -> oi.getQuantity() * oi.getPrice())
                    .sum();
            orderDto.setTotalAmount(totalBeforeDiscount - order.getDiscount());

            return orderDto;
        }).collect(Collectors.toList()));

        return result;
    }

    @Transactional
    public void createOrder(CreateOrderCommand command) {
        // 1. Get cart items
        List<CartItemEntity> cartItems = cartItemRepository.findByUserId(command.getUserId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + command.getUserId());
        }

        // 2. Get item prices
        List<Integer> itemIds = cartItems.stream()
                .map(CartItemEntity::getItemId)
                .collect(Collectors.toList());

        Map<Integer, ItemEntity> itemMap = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, item -> item));

        // 3. Create order
        OrderEntity order = new OrderEntity();
        order.setUserId(command.getUserId());
        order.setDiscount(command.getDiscount() != null ? command.getDiscount() : 0);
        order.setOrderStatus(OrderStatus.NEW.getValue());
        order.setOrderDate(LocalDate.now());
        order = orderRepository.save(order);

        // 4. Create order items
        for (CartItemEntity cartItem : cartItems) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrderId(order.getId());
            orderItem.setItemId(cartItem.getItemId());
            orderItem.setQuantity(cartItem.getQuantity());

            ItemEntity item = itemMap.get(cartItem.getItemId());
            orderItem.setPrice(item != null ? item.getPrice() : 0);

            orderItemRepository.save(orderItem);
        }

        // 5. Clear cart
        cartItemRepository.deleteByUserId(command.getUserId());
    }

    private OrderStatus fromValue(Integer value) {
        if (value == null) return null;
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}

