package com.sejong.aistudyassistant.summary;

import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SummaryService {
    private final TranscriptRepository transcriptRepository;
    private final SummaryRepository summaryRepository;
    private final WebClient webClient;



    public SummaryService(SummaryRepository summaryRepository, TranscriptRepository transcriptRepository, @Value("${daglo.api.token}") String apiToken) {
        this.transcriptRepository = transcriptRepository;
        this.summaryRepository = summaryRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://apis.daglo.ai")
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();
    }

    public Mono<Summary> createSummary(Long transcriptId) {
        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transcript ID: " + transcriptId));

        return webClient.post()
                .uri("/nlp/v1/async/minutes")
                .bodyValue(Map.of("text", transcript.getTranscriptText()))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String rid = (String) response.get("rid");
                    return pollForResult(rid, transcriptId, transcript.getUserId());
                });
    }

    public Summary updateSummary(Long summaryId, String newSummaryText) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Summary not found with id: " + summaryId));

        summary.setSummaryText(newSummaryText);
        return summaryRepository.save(summary);
    }

    private Mono<Summary> pollForResult(String rid, Long transcriptId, Long userId) {
        return webClient.get()
                .uri("/nlp/v1/async/minutes/{rid}", rid)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String status = (String) response.get("status");
                    if ("processed".equals(status)) {
                        List<Map<String, Object>> minutesList = (List<Map<String, Object>>) response.get("minutes");

                        // 요약 텍스트를 저장할 StringBuilder
                        StringBuilder summaryTextBuilder = new StringBuilder();

                        for (Map<String, Object> minute : minutesList) {
                            String title = (String) minute.get("title");
                            summaryTextBuilder.append(title).append(":\n");

                            List<Map<String, Object>> bullets = (List<Map<String, Object>>) minute.get("bullets");
                            for (Map<String, Object> bullet : bullets) {
                                String bulletText = (String) bullet.get("text");
                                summaryTextBuilder.append("- ").append(bulletText).append("\n");
                            }
                            summaryTextBuilder.append("\n"); // 소제목 간의 구분을 위해 줄 바꿈 추가
                        }

                        Summary summary = new Summary();
                        summary.setTranscriptId(transcriptId);
                        summary.setSummaryText(summaryTextBuilder.toString().trim()); // 최종 요약 텍스트 설정
                        summary.setUserId(userId);
                        Summary savedSummary = summaryRepository.save(summary);

                        // Long summaryId = summaryRepository.findByTranscriptId(transcriptId).getId();
                        Transcript newTranscript = transcriptRepository.findById(transcriptId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid transcript ID: " + transcriptId));

                        newTranscript.setSummaryId(savedSummary.getId());
                        transcriptRepository.save(newTranscript);


                        // Save the summary to the database
                        return Mono.just(summary);
                    } else {
                        return Mono.delay(Duration.ofSeconds(5))
                                .flatMap(l -> pollForResult(rid, transcriptId, userId));
                    }
                });
    }


    public Summary getSummaryById(Long summaryId) {
        return summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Summary not found with id: " + summaryId));
    }

    public Summary selfCreateSummary(Long userId,Long subjectId,LocalDateTime createdDate,String text){

        Summary summary=new Summary();
        summary.setSummaryText(text);
        summary.setUserId(userId);
        summary.setSubjectId(subjectId);
        summary.setCreatedAt(createdDate);
        Summary savecSummary=summaryRepository.save(summary);
        return savecSummary;
    }

    public Summary getSummaryByDateAndSubjectId(Long userId, LocalDate date, Long subjectId) {
        return summaryRepository.findByUserIdAndSubjectIdAndCreatedDate(userId, subjectId, date)
                .orElseThrow(() -> new NoSuchElementException(
                        "Summary not found for userId: " + userId + ", date: " + date + ", subjectId: " + subjectId));
    }


}