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
            @PathVariable("date") LocalDate date) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<ReviewScheduleDTO> schedules = reviewScheduleService.findReviewsForDate(userId, date);
        return ResponseEntity.ok(schedules);
    }
}


