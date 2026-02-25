package item.repository.service;

import item.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    List<ItemEntity> findAll();

    List<ItemEntity> findByNameContainingIgnoreCase(String name);

    List<ItemEntity> findByItemType(Integer itemType);

    @Query("SELECT i FROM ItemEntity i WHERE i.id = :id OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ItemEntity> findByIdOrNameContaining(@Param("id") Integer id, @Param("name") String name);
}

