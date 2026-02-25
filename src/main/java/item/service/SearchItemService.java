package item.service;

import org.springframework.stereotype.Service;
import item.entity.ItemEntity;
import item.repository.service.ItemRepository;

import java.util.List;

@Service
public class SearchItemService {

    private final ItemRepository itemRepository;

    public SearchItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemEntity> searchItems(String key) {
        if (key == null || key.isEmpty()) {
            return searchAllItems();
        }
        if (isInteger(key)) {
            return searchItemsByIdOrName(key);
        }
        return searchItemsByName(key);
    }

    private List<ItemEntity> searchAllItems() {
        return itemRepository.findAll();
    }

    private List<ItemEntity> searchItemsByName(String name) {
        return itemRepository.findByName(name);
    }

    private List<ItemEntity> searchItemsByIdOrName(String key) {
        Integer id = Integer.parseInt(key);
        return itemRepository.findByIdOrNameContaining(id, key);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

}
