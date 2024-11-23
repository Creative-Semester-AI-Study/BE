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
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/month-review-stats")
public class MonthStatsController {

    private final ReviewScheduleService reviewScheduleService;
    private final JwtUtil jwtUtil;

    public MonthStatsController(ReviewScheduleService reviewScheduleService, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<TodayStatsDTO> getMonthReviewStats(@RequestHeader("Authorization") String authHeader) {
        // JWT 토큰에서 사용자 ID 추출
        String token = authHeader.replace("Bearer ", ""); // "Bearer " 문자열 제거
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 현재 달의 첫째 날과 마지막 날을 계산
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = YearMonth.from(LocalDate.now()).atEndOfMonth();

        int totalReviews = 0;
        int completedReviews = 0;

        // 해당 달의 모든 날짜에 대해 반복
        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            List<ReviewScheduleDTO> dailySchedules = reviewScheduleService.findReviewsForDate(userId, date);
            totalReviews += dailySchedules.size();
            completedReviews += dailySchedules.stream()
                    .filter(ReviewScheduleDTO::isReviewed)
                    .count();
        }

        // 통계 DTO 생성 및 반환
        TodayStatsDTO stats = new TodayStatsDTO(totalReviews, completedReviews);
        return ResponseEntity.ok(stats);
    }
}
