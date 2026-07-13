package com.example.voice.commanding.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ShoppingListDTO {

    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ListItemDTO> items;
    private int itemCount;
    private BigDecimal totalEstimatedCost;
}
