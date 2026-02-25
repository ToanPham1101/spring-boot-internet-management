package internet.management.model;

import lombok.Data;

@Data
public class UpdateCartQuantityCommand {
    private Integer userId;
    private Integer itemId;
    private Integer quantity;
}

