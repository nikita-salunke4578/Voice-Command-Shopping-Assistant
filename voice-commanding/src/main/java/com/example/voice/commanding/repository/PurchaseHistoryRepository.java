package com.example.voice.commanding.repository;

import com.example.voice.commanding.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    List<PurchaseHistory> findTop20ByOrderByPurchaseCountDesc();

    List<PurchaseHistory> findByProductNameIgnoreCase(String productName);

    List<PurchaseHistory> findAllByOrderByPurchaseCountDesc();
}
