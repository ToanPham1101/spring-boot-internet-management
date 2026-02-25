package internet.management.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionResult {
    private Integer id;
    private Integer userId;
    private String username;
    private String category;
    private Integer pricePerHour;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long usedMinutes;
    private Integer cost;

    @Data
    public static class TimeRemainingResult {
        private Integer userId;
        private String username;
        private String category;
        private Integer balance;
        private Integer pricePerHour;
        private Integer remainingHours;
        private Integer remainingMinutes;
        private String remainingTimeFormatted;
        private boolean hasActiveSession;
        private LocalDateTime sessionStartTime;
        private Long sessionUsedMinutes;
    }
}

