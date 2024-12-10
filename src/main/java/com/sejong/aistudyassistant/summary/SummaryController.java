package com.sejong.aistudyassistant.summary;


import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.summary.dto.GetSummaryRequest;
import com.sejong.aistudyassistant.summary.dto.SelfSummaryCreateRequest;
import com.sejong.aistudyassistant.summary.dto.SummaryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/summaries")
public class SummaryController {
    private final SummaryService summaryService;
    private final JwtUtil jwtUtil;


    public SummaryController(SummaryService summaryService,JwtUtil jwtUtil) {
        this.summaryService = summaryService;
        this.jwtUtil=jwtUtil;
    }

    @PostMapping
    public Mono<ResponseEntity<SummaryResponseDto>> createSummary(@RequestBody Map<String, Long> request) {
        Long transcriptId = request.get("transcriptId");
        return summaryService.createSummary(transcriptId)
                .map(summary -> ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId(),summary.getUserId())));
    }

    @GetMapping("/{summaryId}")
    public ResponseEntity<SummaryResponseDto> getSummaryById(@PathVariable("summaryId") Long summaryId) {
        Summary summary = summaryService.getSummaryById(summaryId);
        return ResponseEntity.ok(new SummaryResponseDto(summary.getId(), summary.getSummaryText(), summary.getTranscriptId(), summary.getUserId()));
    }

    @PutMapping("/{summaryId}")
    public ResponseEntity<SummaryResponseDto> updateSummary(@PathVariable("summaryId") Long summaryId, @RequestBody Map<String, String> updateData) {
        String newSummaryText = updateData.get("summaryText");
        Summary updatedSummary = summaryService.updateSummary(summaryId, newSummaryText);
        return ResponseEntity.ok(new SummaryResponseDto(updatedSummary.getId(), updatedSummary.getSummaryText(), updatedSummary.getTranscriptId(), updatedSummary.getUserId()));
    }

    @PostMapping("/self/create")
    public ResponseEntity<Summary> selfCreateSummary(@RequestHeader("Authorization") String authHeader,@RequestBody SelfSummaryCreateRequest request){

        String token = authHeader.replace("Bearer ", "");
        Long userId=jwtUtil.getUserIdFromToken(token);
        return ResponseEntity.ok(summaryService.selfCreateSummary(userId,request.subjectId(),request.createdDate(),request.summary()));
    }


    @PostMapping("/find/summary")
    public ResponseEntity<Summary> getSummaryByDateAndSubjectId(@RequestHeader("Authorization") String authHeader,
                                                                @RequestBody GetSummaryRequest request) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        Summary summary = summaryService.getSummaryByDateAndSubjectId(userId, request.date(), request.subjectId());
        return ResponseEntity.ok(summary);
    }

}