package item.repository;

import item.entity.CartItemEntity;
import item.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, CartItemId> {

    List<CartItemEntity> findByUserId(Integer userId);

    @Transactional
    void deleteByUserId(Integer userId);
}

