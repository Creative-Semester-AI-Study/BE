package com.sejong.aistudyassistant.summary;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/summaries")
public class SummaryController {
    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping
    public Mono<ResponseEntity<SummaryResponseDto>> createSummary(@RequestBody Map<String, Long> request) {
        Long transcriptId = request.get("transcriptId");
        return summaryService.createSummary(transcriptId)
                .map(summary -> ResponseEntity.ok(new SummaryResponseDto(summary.getSummaryText(), summary.getTranscriptId())));
    }
}