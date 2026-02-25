package internet.management.model;

import java.util.List;

import lombok.Data;

@Data
public class GetCartResult {

    private List<Item> items;

    @Data
    public static class Item {

        private Integer itemId;

        private String itemName;

        private Integer itemPrice;

        private Integer quantity;
    }
}
