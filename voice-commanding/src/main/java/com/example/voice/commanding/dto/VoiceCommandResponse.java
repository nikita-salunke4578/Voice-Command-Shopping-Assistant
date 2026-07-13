package com.example.voice.commanding.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VoiceCommandResponse {

    private String action;       
    private String message;     
    private boolean success;
    private boolean hasUnavailableItems;
    private List<ListItemDTO> updatedItems;    
    private List<ProductDTO> searchResults;    
    private List<SuggestionDTO> suggestions;   
}
