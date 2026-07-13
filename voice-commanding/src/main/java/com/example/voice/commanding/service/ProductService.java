package com.example.voice.commanding.service;

import com.example.voice.commanding.dto.ProductDTO;
import com.example.voice.commanding.model.Product;
import com.example.voice.commanding.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> searchProducts(String query, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Searching products: query={}, category={}, minPrice={}, maxPrice={}", query, category, minPrice, maxPrice);
        return productRepository.searchProducts(query, category, minPrice, maxPrice)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public List<ProductDTO> getSeasonalProducts() {
        return productRepository.findByIsSeasonalTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .brand(product.getBrand())
                .price(product.getPrice())
                .size(product.getSize())
                .isSeasonal(product.getIsSeasonal())
                .isAvailable(product.getIsAvailable())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
