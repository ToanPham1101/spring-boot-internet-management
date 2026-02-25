package internet.management.repository;

import internet.management.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    @Query("SELECT o FROM OrderEntity o WHERE " +
            "(:userId IS NULL OR o.userId = :userId) AND " +
            "(:orderStatus IS NULL OR o.orderStatus = :orderStatus)")
    List<OrderEntity> searchOrders(@Param("userId") Integer userId,
                                   @Param("orderStatus") Integer orderStatus);
}

