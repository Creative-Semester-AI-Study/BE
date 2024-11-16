package com.sejong.aistudyassistant.stt;

import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TranscriptionService {
    private final TranscriptRepository transcriptRepository;

    private final SubjectRepository subjectRepository;
    private final WebClient webClient;

    public TranscriptionService(
            TranscriptRepository transcriptRepository,
            SubjectRepository subjectRepository,
            @Value("${daglo.api.token}") String apiToken) {
        this.transcriptRepository = transcriptRepository;
        this.subjectRepository = subjectRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://apis.daglo.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .build();
    }

    public Mono<String> transcribeAudio(String audioUrl) {
        return webClient.post()
                .uri("/stt/v1/async/transcripts")
                .bodyValue(Map.of("audio", Map.of("source", Map.of("url", audioUrl))))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String rid = (String) response.get("rid");
                    return pollForResult(rid);
                });
    }

    private Mono<String> pollForResult(String rid) {
        return webClient.get()
                .uri("/stt/v1/async/transcripts/{rid}", rid)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String status = (String) response.get("status");
                    if ("transcribed".equals(status)) {
                        List<Map<String, Object>> sttResults = (List<Map<String, Object>>) response.get("sttResults");
                        String transcript = (String) sttResults.get(0).get("transcript");
                        return Mono.just(transcript);
                    } else {
                        return Mono.delay(Duration.ofSeconds(5))
                                .flatMap(l -> pollForResult(rid));
                    }
                });
    }

    public Transcript saveTranscript(Long subjectId, Long userId, String audioFileName, String transcriptText) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject ID: " + subjectId));

        Transcript transcript = new Transcript();
        transcript.setSubject(subject);
        transcript.setUserId(userId);
        transcript.setAudioFileName(audioFileName);
        transcript.setTranscriptText(transcriptText);
        transcript.setCreatedAt(LocalDateTime.now());

        return transcriptRepository.save(transcript);
    }

    public TranscriptDTO getTranscriptById(Long transcriptId, Long userId) {
        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new RuntimeException("Transcript not found with id: " + transcriptId));

        // 보안을 위해 요청한 사용자의 userId와 transcript의 userId가 일치하는지 확인
        if (!transcript.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to transcript");
        }

        return convertToDTO(transcript);
    }

    private TranscriptDTO convertToDTO(Transcript transcript) {
        return new TranscriptDTO(
                transcript.getId(),
                transcript.getSubject().getId(),
                transcript.getAudioFileName(),
                transcript.getTranscriptText(),
                transcript.getCreatedAt(),
                transcript.getUserId(),
                transcript.getSummaryId(),
                transcript.getQuizId()
        );
    }
}