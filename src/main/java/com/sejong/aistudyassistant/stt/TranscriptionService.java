package com.sejong.aistudyassistant.stt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
/*
@Service
public class TranscriptionService {
    private final TranscriptRepository transcriptRepository;
    private final String apiToken;
    private final WebClient webClient;

    public TranscriptionService(
            TranscriptRepository transcriptRepository,
            @Value("${daglo.api.token}") String apiToken) {
        this.transcriptRepository = transcriptRepository;
        this.apiToken = apiToken;
        this.webClient = WebClient.builder()
                .baseUrl("https://apis.daglo.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .build();
    }

    @PostConstruct
    public void init() {
        System.out.println("API Token (first 5 chars): " +
                (apiToken != null ? apiToken.substring(0, Math.min(apiToken.length(), 5)) : "null"));
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

    public Transcript saveTranscript(String audioFileName, String transcriptText) {
        Transcript transcript = new Transcript();
        transcript.setAudioFileName(audioFileName);
        transcript.setTranscriptText(transcriptText);
        transcript.setCreatedAt(LocalDateTime.now());
        return transcriptRepository.save(transcript);
    }
}
*/
