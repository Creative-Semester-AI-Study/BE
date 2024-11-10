package com.sejong.aistudyassistant.stt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {
    private final TranscriptionService transcriptionService;

    public TranscriptionController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping
    public Mono<ResponseEntity<Transcript>> transcribeAudio(@RequestBody Map<String, String> request) {
        String audioUrl = request.get("audioUrl");
        String audioFileName = request.get("audioFileName");
        return transcriptionService.transcribeAudio(audioUrl)
                .map(transcriptText -> transcriptionService.saveTranscript(audioFileName, transcriptText))
                .map(ResponseEntity::ok);
    }
}
