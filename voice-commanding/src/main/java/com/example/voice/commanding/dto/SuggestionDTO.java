package com.example.voice.commanding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuggestionDTO {

    private String name;
    private String reason;
    private String category;
    private String type;  // "history", "seasonal", "substitute"
}
