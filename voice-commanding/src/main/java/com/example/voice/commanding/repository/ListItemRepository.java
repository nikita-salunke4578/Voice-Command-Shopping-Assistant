package com.example.voice.commanding.repository;

import com.example.voice.commanding.model.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, Long> {

    List<ListItem> findByShoppingListIdOrderByCategoryAscItemNameAsc(Long shoppingListId);

    List<ListItem> findByShoppingListId(Long shoppingListId);

    boolean existsByShoppingListIdAndItemNameIgnoreCase(Long shoppingListId, String itemName);

    List<ListItem> findByShoppingListIdAndItemNameContainingIgnoreCase(Long shoppingListId, String itemName);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ListItem item WHERE item.id = :itemId AND item.shoppingList.id = :listId")
    int deleteFromList(@Param("listId") Long listId, @Param("itemId") Long itemId);
}
