package item.entity;

import java.io.Serializable;
import java.util.Objects;

public class CartItemId implements Serializable {

    private Integer userId;
    private Integer itemId;

    public CartItemId(Integer userId, Integer itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }
}

