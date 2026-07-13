package com.example.voice.commanding.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "list_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    private String unit;

    private String category;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "is_checked")
    @Builder.Default
    private Boolean isChecked = false;

    @Column(name = "added_at", updatable = false)
    @Builder.Default
    private LocalDateTime addedAt = LocalDateTime.now();
}
