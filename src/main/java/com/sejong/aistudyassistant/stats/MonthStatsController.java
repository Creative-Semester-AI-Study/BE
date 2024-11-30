package com.sejong.aistudyassistant.stats;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.schedule.ReviewScheduleDTO;
import com.sejong.aistudyassistant.schedule.ReviewScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //이번년도 달에 대해서만 통계를 냄
    @GetMapping
    public ResponseEntity<MonthStatsDTO> getMonthReviewStats(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {

        // JWT 토큰에서 사용자 ID 추출
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 사용자가 특정 월을 지정하지 않은 경우, 현재 월을 사용
        if (month == null) {
            month = LocalDate.now();
        }
        YearMonth yearMonth = YearMonth.from(month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        int monthTotalReviews = 0;
        int monthCompletedReviews = 0;
        int monthReviewPercentage = 0;

        // 이번년도 특정 월의 모든 날짜에 대해 총 통계의 수 세고, 복습 완료한 개수를 셈
        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            List<ReviewScheduleDTO> dailySchedules = reviewScheduleService.findReviewsForDate(userId, date);
            monthTotalReviews += dailySchedules.size();
            monthCompletedReviews += dailySchedules.stream()
                    .filter(ReviewScheduleDTO::isReviewed)
                    .count();
        }

        monthReviewPercentage = (int) Math.round(((double) monthCompletedReviews / monthTotalReviews) * 100);

        // 통계 DTO 생성 및 반환
        MonthStatsDTO stats = new MonthStatsDTO(monthTotalReviews, monthCompletedReviews, monthReviewPercentage);
        return ResponseEntity.ok(stats);
    }
}
