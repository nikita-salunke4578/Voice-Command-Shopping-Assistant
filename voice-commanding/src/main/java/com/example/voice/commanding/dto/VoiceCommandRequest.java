package com.example.voice.commanding.dto;


import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VoiceCommandRequest {


    private String text;

    @Builder.Default
    private String language = "en";
    
    private Long listId;
}
