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
@RequestMapping("/api/stats")
public class StatsController {

    private final ReviewScheduleService reviewScheduleService;
    private final JwtUtil jwtUtil;

    public StatsController(ReviewScheduleService reviewScheduleService, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.jwtUtil = jwtUtil;
    }

    // 오늘의 복습 통계
    @GetMapping("/today")
    public ResponseEntity<TodayStatsDTO> getTodayStats(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        LocalDate today = LocalDate.now();
        List<ReviewScheduleDTO> schedules = reviewScheduleService.findReviewsForDate(userId, today);

        int totalReviews = schedules.size();
        int completedReviews = (int) schedules.stream()
                .filter(ReviewScheduleDTO::isReviewed)
                .count();

        TodayStatsDTO stats = new TodayStatsDTO(totalReviews, completedReviews);
        return ResponseEntity.ok(stats);
    }

    // 특정 달의 복습 통계
    @GetMapping("/month/{month}")
    public ResponseEntity<MonthStatsDTO> getMonthStats(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("month") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (month == null) {
            month = LocalDate.now();
        }

        YearMonth yearMonth = YearMonth.from(month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        double totalDailyReviewRate = 0.0; // 모든 날의 복습률 합
        int totalDays = 0; // 총 일수

        //특정 달 모든 날의 복습률을 구해서 totalDailyReviewRate에 더함
        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            List<ReviewScheduleDTO> dailySchedules = reviewScheduleService.findReviewsForDate(userId, date);

            int dailyTotalReviews = dailySchedules.size();
            if (dailyTotalReviews > 0) {
                int dailyCompletedReviews = (int) dailySchedules.stream()
                        .filter(ReviewScheduleDTO::isReviewed)
                        .count();

                // 해당 날짜의 복습률 계산
                double dailyReviewRate = (double) dailyCompletedReviews / dailyTotalReviews;
                totalDailyReviewRate += dailyReviewRate; // 복습률 누적
            }
            totalDays++; // 총 일수 증가
        }

        // totalDailyReviewRate를 전체 일 수로 나눠서 평균 복습률 계산
        int averageReviewPercentage = totalDays > 0
                ? (int) Math.round((totalDailyReviewRate / totalDays) * 100)
                : 0;

        // MonthStatsDTO 생성 및 반환
        MonthStatsDTO stats = new MonthStatsDTO(averageReviewPercentage);
        return ResponseEntity.ok(stats);
    }


    // 모든 과목의 복습 통계
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectStatsDTO>> getSubjectStats(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<SubjectStatsDTO> subjectStatsList = reviewScheduleService.getAllSubjectsStats(userId);
        return ResponseEntity.ok(subjectStatsList);
    }
}
