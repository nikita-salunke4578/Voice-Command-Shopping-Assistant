package com.example.voice.commanding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String size;
    private Boolean isSeasonal;
    private Boolean isAvailable;
    private String imageUrl;
}
