package com.example.voice.commanding.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ListItemDTO {

    private Long id;
    private Long shoppingListId;
    private Long productId;
    private String itemName;
    private Integer quantity;
    private String unit;
    private String category;
    private BigDecimal estimatedPrice;
    private Boolean isChecked;
    private LocalDateTime addedAt;
    private String imageUrl;
}
