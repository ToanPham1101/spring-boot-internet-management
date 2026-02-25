package item.service;

import item.entity.CartItemEntity;
import item.entity.CartItemId;
import item.entity.ItemEntity;
import item.model.GetCartQuery;
import item.model.GetCartResult;
import item.model.UpdateCartQuantityCommand;
import item.repository.CartItemRepository;
import item.repository.service.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    public CartService(CartItemRepository cartItemRepository, ItemRepository itemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
    }

    public GetCartResult getCart(GetCartQuery query) {
        List<CartItemEntity> cartItems = cartItemRepository.findByUserId(query.getUserId());

        // Get all item ids from cart
        List<Integer> itemIds = cartItems.stream()
                .map(CartItemEntity::getItemId)
                .collect(Collectors.toList());

        // Fetch item details
        Map<Integer, ItemEntity> itemMap = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, item -> item));

        // Build result
        GetCartResult result = new GetCartResult();
        result.setItems(cartItems.stream().map(cartItem -> {
            GetCartResult.Item item = new GetCartResult.Item();
            item.setItemId(cartItem.getItemId());
            item.setQuantity(cartItem.getQuantity());

            ItemEntity itemEntity = itemMap.get(cartItem.getItemId());
            if (itemEntity != null) {
                item.setItemName(itemEntity.getName());
                item.setItemPrice(itemEntity.getPrice());
            }
            return item;
        }).collect(Collectors.toList()));

        return result;
    }

    @Transactional
    public void updateQuantity(UpdateCartQuantityCommand command) {
        CartItemId id = new CartItemId(command.getUserId(), command.getItemId());

        if (command.getQuantity() == null || command.getQuantity() <= 0) {
            // Remove item from cart
            cartItemRepository.deleteById(id);
        } else {
            // Insert or update
            CartItemEntity cartItem = cartItemRepository.findById(id).orElse(null);
            if (cartItem == null) {
                cartItem = new CartItemEntity();
                cartItem.setUserId(command.getUserId());
                cartItem.setItemId(command.getItemId());
            }
            cartItem.setQuantity(command.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }
}

