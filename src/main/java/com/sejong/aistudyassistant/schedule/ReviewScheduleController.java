package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewScheduleController {
    private final ReviewScheduleService reviewScheduleService;
    private final JwtUtil jwtUtil;  // JWT 유틸리티 클래스를 주입받습니다.

    public ReviewScheduleController(ReviewScheduleService reviewScheduleService, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<ReviewScheduleDTO>> getReviewSchedules(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = authHeader.replace("Bearer ", "");  // 헤더에서 토큰을 추출합니다.
        Long userId = jwtUtil.getUserIdFromToken(token);  // 토큰에서 사용자 ID를 추출합니다.

        List<ReviewScheduleDTO> schedules = reviewScheduleService.findReviewsForDate(userId, date);
        return ResponseEntity.ok(schedules);
    }
}


