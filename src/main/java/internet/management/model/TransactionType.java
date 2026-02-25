package internet.management.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT(1),
    ORDER_PAYMENT(2),
    SESSION_PAYMENT(3);

    private final int value;

    public static String nameOf(int value) {
        for (TransactionType t : values()) {
            if (t.value == value) return t.name();
        }
        return "UNKNOWN";
    }
}

