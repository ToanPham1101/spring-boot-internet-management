package item.model;

import lombok.Data;

@Data
public class UpdateCartQuantityCommand {
    private Integer userId;
    private Integer itemId;
    private Integer quantity;
}

