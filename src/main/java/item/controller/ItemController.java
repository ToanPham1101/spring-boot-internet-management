package item.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import item.entity.ItemEntity;
import item.model.SearchItemsQuery;
import item.model.SearchItemsResult;
import item.service.SearchItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final SearchItemService searchItemService;

    public ItemController(SearchItemService searchItemService) {
        this.searchItemService = searchItemService;
    }

    @GetMapping("/search")
    public SearchItemsResult searchItems(@ParameterObject SearchItemsQuery query) {
        List<ItemEntity> items = searchItemService.searchItems(query.toString());

        // Setting lombok for working properly
        SearchItemsResult result = new SearchItemsResult();
        result.setItems(items.stream().map(entity -> {
            SearchItemsResult.Item item = new SearchItemsResult.Item();
            item.setId(entity.getId());
            item.setName(entity.getName());
            return item;
        }).collect(Collectors.toList()));

        return result;
    }
}
