package item.model;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SearchOrdersQuery {

    @Nullable
    private String userId;

    @Nullable
    private OrderStatus orderStatus;
}
