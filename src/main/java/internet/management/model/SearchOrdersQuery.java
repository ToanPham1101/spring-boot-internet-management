package internet.management.model;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SearchOrdersQuery {

    @Nullable
    private Integer userId;

    @Nullable
    private OrderStatus orderStatus;
}

