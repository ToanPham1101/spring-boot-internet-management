package item.model;

import lombok.Data;

@Data
public class CreateOrderCommand {
    private String userId;
    private Integer discount;
}
