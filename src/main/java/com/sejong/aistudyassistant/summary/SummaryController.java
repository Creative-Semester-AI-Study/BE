package com.sejong.aistudyassistant.summary;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
                .map(summary -> ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId(),summary.getUserId())));
    }

    @GetMapping("/{summaryId}")
    public ResponseEntity<SummaryResponseDto> getSummaryById(@PathVariable Long summaryId) {
        Summary summary = summaryService.getSummaryById(summaryId);
        return ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId(), summary.getUserId()));
    }

    @PutMapping("/{summaryId}")
    public ResponseEntity<SummaryResponseDto> updateSummary(@PathVariable Long summaryId, @RequestBody Map<String, String> updateData) {
        String newSummaryText = updateData.get("summaryText");
        Summary updatedSummary = summaryService.updateSummary(summaryId, newSummaryText);
        return ResponseEntity.ok(new SummaryResponseDto(updatedSummary.getId(), updatedSummary.getSummaryText(), updatedSummary.getTranscriptId(), updatedSummary.getUserId()));
    }
}