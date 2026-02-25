package internet.management.model;

import lombok.Data;

@Data
public class DepositCommand {
    private Integer userId;
    private Integer amount;
    private String description;
}

