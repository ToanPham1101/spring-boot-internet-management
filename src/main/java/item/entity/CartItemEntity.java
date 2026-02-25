package item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@IdClass(CartItemId.class)
@Table(name = "cart_item")
public class CartItemEntity {

    @Id
    @Column(name = "user_id", length = 16)
    private String userId;

    @Id
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(nullable = false)
    private Integer quantity;
}

