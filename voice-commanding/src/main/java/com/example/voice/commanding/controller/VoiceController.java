package com.example.voice.commanding.controller;

import com.example.voice.commanding.dto.VoiceCommandRequest;
import com.example.voice.commanding.dto.VoiceCommandResponse;
import com.example.voice.commanding.service.VoiceCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceCommandService voiceCommandService;

    @PostMapping("/process")
    public ResponseEntity<VoiceCommandResponse> processCommand(@Valid @RequestBody VoiceCommandRequest request) {
        VoiceCommandResponse response = voiceCommandService.processCommand(request);
        return ResponseEntity.ok(response);
    }
}
