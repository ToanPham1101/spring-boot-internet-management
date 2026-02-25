package internet.management.repository;

import internet.management.entity.OrderItemEntity;
import internet.management.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemId> {

    List<OrderItemEntity> findByOrderId(Integer orderId);

    List<OrderItemEntity> findByOrderIdIn(List<Integer> orderIds);
}

