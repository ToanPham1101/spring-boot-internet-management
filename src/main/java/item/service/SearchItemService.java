package item.service;

import item.entity.ItemEntity;
import item.repository.service.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchItemService {

    private final ItemRepository itemRepository;

    public SearchItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemEntity> searchItems(String key) {
        if (key == null || key.isEmpty()) {
            return itemRepository.findAll();
        }
        if (isInteger(key)) {
            return itemRepository.findByIdOrNameContaining(Integer.parseInt(key), key);
        }
        return itemRepository.findByNameContainingIgnoreCase(key);
    }

    public List<ItemEntity> searchByType(Integer itemType) {
        return itemRepository.findByItemType(itemType);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}

