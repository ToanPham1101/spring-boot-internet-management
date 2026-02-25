package internet.management.entity;

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
@IdClass(OrderItemId.class)
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Id
    @Column(name = "item_id")
    private Integer itemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;
}

