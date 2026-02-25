package item.model;

import lombok.Data;

@Data
public class UpdateCartQuantityCommand {
    private String userId;
    private Integer itemId;
    private Integer quantity;
}
