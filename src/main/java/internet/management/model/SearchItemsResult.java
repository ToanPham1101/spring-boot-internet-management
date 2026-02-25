package internet.management.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchItemsResult {

    private List<Item> items;

    @Data
    public static class Item {
        private Integer id;
        private String name;
        private Integer price;
        private String itemType;
    }
}

