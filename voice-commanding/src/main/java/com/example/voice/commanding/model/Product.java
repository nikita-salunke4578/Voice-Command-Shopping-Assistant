package com.example.voice.commanding.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    private String brand;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private String size;

    @Column(name = "is_seasonal")
    @Builder.Default
    private Boolean isSeasonal = false;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "image_url")
    private String imageUrl;
}
