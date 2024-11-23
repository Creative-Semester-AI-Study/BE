package com.sejong.aistudyassistant.stats;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.schedule.ReviewScheduleDTO;
import com.sejong.aistudyassistant.schedule.ReviewScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/today-review-stats")
public class TodayStatsController {

    private final ReviewScheduleService reviewScheduleService;
    private final JwtUtil jwtUtil;

    public TodayStatsController(ReviewScheduleService reviewScheduleService, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<TodayStatsDTO> getReviewStats(@RequestHeader("Authorization") String authHeader) {
        // JWT 토큰에서 사용자 ID 추출
        String token = authHeader.replace("Bearer ", ""); // "Bearer " 문자열 제거
        Long userId = jwtUtil.getUserIdFromToken(token);

        LocalDate today = LocalDate.now();  // 오늘 날짜를 가져옵니다.
        List<ReviewScheduleDTO> schedules = reviewScheduleService.findReviewsForDate(userId, today);

        // 전체 복습 개수
        int totalReviews = schedules.size();

        // 복습 완료된 개수
        int completedReviews = (int) schedules.stream()
                .filter(ReviewScheduleDTO::isReviewed)
                .count();

        // 통계 DTO 생성 및 반환
        TodayStatsDTO stats = new TodayStatsDTO(totalReviews, completedReviews);
        return ResponseEntity.ok(stats);
    }
}
