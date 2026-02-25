package internet.management.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionStatus {
    ACTIVE(1),
    EXPIRED(2),
    CANCELLED(3);

    private final int value;

    public static String nameOf(int value) {
        for (SessionStatus s : values()) {
            if (s.value == value) return s.name();
        }
        return "UNKNOWN";
    }
}

