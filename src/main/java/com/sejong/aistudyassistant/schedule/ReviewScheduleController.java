package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/{date}")
    public ResponseEntity<List<ReviewScheduleDTO>> getReviewSchedules(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String date) {  // 날짜를 경로에서 받음
        String token = authHeader.replace("Bearer ", "");  // 헤더에서 토큰 추출
        Long userId = jwtUtil.getUserIdFromToken(token);  // 토큰에서 사용자 ID 추출

        // URL 경로에서 받은 날짜 문자열을 LocalDate로 변환
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

        List<ReviewScheduleDTO> schedules = reviewScheduleService.findReviewsForDate(userId, localDate);
        return ResponseEntity.ok(schedules);
    }
}


