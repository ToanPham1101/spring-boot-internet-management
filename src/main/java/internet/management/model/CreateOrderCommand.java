package internet.management.model;

import lombok.Data;

@Data
public class CreateOrderCommand {
    private Integer userId;
    private Integer discount;
}

