package internet.management.repository;

import internet.management.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Integer> {

    Optional<SessionEntity> findByUserIdAndStatus(Integer userId, Integer status);

    List<SessionEntity> findByUserIdOrderByStartTimeDesc(Integer userId);
}

