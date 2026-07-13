package com.example.voice.commanding.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    private String category;

    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "purchase_count")
    @Builder.Default
    private Integer purchaseCount = 1;

    @Column(name = "last_purchased")
    @Builder.Default
    private LocalDateTime lastPurchased = LocalDateTime.now();
}
