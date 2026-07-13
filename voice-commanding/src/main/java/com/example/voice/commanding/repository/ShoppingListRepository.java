package com.example.voice.commanding.repository;

import com.example.voice.commanding.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    List<ShoppingList> findByIsActiveTrueOrderByUpdatedAtDesc();

    List<ShoppingList> findAllByOrderByUpdatedAtDesc();
}
