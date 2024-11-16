package com.sejong.aistudyassistant.stt;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {
    private final TranscriptionService transcriptionService;
    private final JwtUtil jwtUtil;

    TranscriptionController(TranscriptionService transcriptionService, JwtUtil jwtUtil) {
        this.transcriptionService = transcriptionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Mono<ResponseEntity<Transcript>> transcribeAudio(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        String audioUrl = request.get("audioUrl");
        String audioFileName = request.get("audioFileName");
        Long subjectId = Long.parseLong(request.get("subjectId"));

        return transcriptionService.transcribeAudio(audioUrl)
                .map(transcriptText -> transcriptionService.saveTranscript(subjectId, userId, audioFileName, transcriptText))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{transcriptId}")
    public ResponseEntity<TranscriptDTO> getTranscriptById(
            @PathVariable Long transcriptId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        try {
            TranscriptDTO transcript = transcriptionService.getTranscriptById(transcriptId, userId);
            return ResponseEntity.ok(transcript);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}