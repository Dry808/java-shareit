package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    String FIND_ITEMS_BY_NAME_AND_DESCR = "SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))";

    List<Item> findByOwnerId(int userId);

    @Query(FIND_ITEMS_BY_NAME_AND_DESCR)
    List<Item> findItemByNameAndDescription(@Param("text") String text);

    boolean existsByOwner_Id(int ownerId);
}
