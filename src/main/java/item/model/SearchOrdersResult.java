package item.model;

import java.util.List;

import lombok.Data;

@Data
public class SearchOrdersResult {

    private List<Order> orders;

    @Data
    public static class Order {
        private Integer orderId;
        private String userId;
        private OrderStatus orderStatus;
        private Integer discount;
        private Integer totalAmount;
        List<OrderItem> items;
    }

    @Data
    public static class OrderItem {
        private Integer itemId;
        private String itemName;
        private Integer quantity;
        private Integer price;
    }
}
