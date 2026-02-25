package internet.management.controller;

import internet.management.entity.ItemEntity;
import internet.management.model.ItemType;
import internet.management.model.SearchItemsQuery;
import internet.management.model.SearchItemsResult;
import internet.management.service.SearchItemService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internet/management")
public class ItemController {

    private final SearchItemService searchItemService;

    public ItemController(SearchItemService searchItemService) {
        this.searchItemService = searchItemService;
    }

    @GetMapping("/search")
    public SearchItemsResult searchItems(@ParameterObject SearchItemsQuery query) {
        List<ItemEntity> items = searchItemService.searchItems(query.toString());
        return toResult(items);
    }

    @GetMapping("/food")
    public SearchItemsResult getFood() {
        List<ItemEntity> items = searchItemService.searchByType(ItemType.FOOD.getValue());
        return toResult(items);
    }

    @GetMapping("/drink")
    public SearchItemsResult getDrink() {
        List<ItemEntity> items = searchItemService.searchByType(ItemType.DRINK.getValue());
        return toResult(items);
    }

    private SearchItemsResult toResult(List<ItemEntity> items) {
        SearchItemsResult result = new SearchItemsResult();
        result.setItems(items.stream().map(entity -> {
            SearchItemsResult.Item item = new SearchItemsResult.Item();
            item.setId(entity.getId());
            item.setName(entity.getName());
            item.setPrice(entity.getPrice());
            item.setItemType(entity.getItemType() != null
                    ? ItemType.nameOf(entity.getItemType()) : null);
            return item;
        }).collect(Collectors.toList()));
        return result;
    }
}

