package com.sejong.aistudyassistant.mypage;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.stt.TranscriptDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/study/myPage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    private final JwtUtil jwtUtil;

    MyPageController(MyPageService myPageService, JwtUtil jwtUtil) {
        this.myPageService = myPageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/transcripts/{subjectId}")
    public ResponseEntity<List<TranscriptDTO>> getTranscriptsBySubjectId(
            @PathVariable("subjectId") Long subjectId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<TranscriptDTO> transcripts = myPageService.getTranscriptsByUserIdAndSubjectId(userId, subjectId);
        return ResponseEntity.ok(transcripts);
    }

    @GetMapping("/transcripts/date/{date}")
    public ResponseEntity<List<TranscriptDTO>> getTranscriptsByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<TranscriptDTO> transcripts = myPageService.getTranscriptsByUserIdAndDate(userId, date);
        return ResponseEntity.ok(transcripts);
    }
}


