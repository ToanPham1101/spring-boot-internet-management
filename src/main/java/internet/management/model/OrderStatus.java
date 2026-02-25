package internet.management.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    NEW(1),
    DONE(2),
    CANCEL(3),
    ;

    private final int value;
}
