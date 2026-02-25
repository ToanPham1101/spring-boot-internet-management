package internet.management.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {
    FOOD(1),
    DRINK(2);

    private final int value;

    public static String nameOf(int value) {
        for (ItemType t : values()) {
            if (t.value == value) return t.name();
        }
        return "UNKNOWN";
    }
}

