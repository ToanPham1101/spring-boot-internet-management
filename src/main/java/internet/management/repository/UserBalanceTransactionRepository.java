package internet.management.repository;

import internet.management.entity.UserBalanceTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBalanceTransactionRepository extends JpaRepository<UserBalanceTransactionEntity, Integer> {

    List<UserBalanceTransactionEntity> findByUserIdOrderByCreatedAtDesc(Integer userId);
}

