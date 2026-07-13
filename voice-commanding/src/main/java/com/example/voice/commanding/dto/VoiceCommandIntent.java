package com.example.voice.commanding.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceCommandIntent {
    private String action; // ADD, REMOVE, MODIFY, SEARCH
    private String message;
    private String translatedText;

    @JsonAlias({"items_to_add", "ItemsToAdd"})
    private List<ParsedItem> itemsToAdd;

    @JsonAlias({"items_to_remove", "ItemsToRemove"})
    private List<ParsedItem> itemsToRemove;

    @JsonAlias({"items_to_modify", "ItemsToModify"})
    private List<ParsedItem> itemsToModify;

    @JsonAlias({"search_results", "SearchResults"})
    private List<ProductDTO> searchResults;

    private List<SuggestionDTO> recommendations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParsedItem {
        private String name;
        private String requestedName;
        private String catalogProductName;
        private Integer quantity;
        private String unit;
        private String category;

        @JsonAlias({"estimated_price", "estimatedPrice"})
        private java.math.BigDecimal estimatedPrice;
    }
}
