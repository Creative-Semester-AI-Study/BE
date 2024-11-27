package com.sejong.aistudyassistant.summary;

import com.sejong.aistudyassistant.summary.dto.SummaryResponseDto;
import org.springframework.http.ResponseEntity;
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
                .map(summary -> ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId())));
    }

    @GetMapping("/{summaryId}")
    public ResponseEntity<SummaryResponseDto> getSummaryById(@PathVariable Long summaryId) {
        Summary summary = summaryService.getSummaryById(summaryId);
        return ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId()));
    }
}