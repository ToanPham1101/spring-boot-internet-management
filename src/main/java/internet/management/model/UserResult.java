package internet.management.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResult {
    private Integer id;
    private String username;
    private String fullName;
    private Integer balance;
    private String category;
    private Integer pricePerHour;
    private Integer remainingHours;
    private Integer remainingMinutes;
    private String remainingTimeFormatted;
    private LocalDateTime createdAt;
    private List<TransactionResult> recentTransactions;

    @Data
    public static class TransactionResult {
        private Integer id;
        private Integer amount;
        private String type;
        private String description;
        private LocalDateTime createdAt;
    }
}

