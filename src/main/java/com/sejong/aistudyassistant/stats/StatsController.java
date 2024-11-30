package com.sejong.aistudyassistant.stats;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.schedule.ReviewScheduleDTO;
import com.sejong.aistudyassistant.schedule.ReviewScheduleService;
import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final ReviewScheduleService reviewScheduleService;
    private final SubjectRepository subjectRepository;
    private final JwtUtil jwtUtil;

    public StatsController(ReviewScheduleService reviewScheduleService,SubjectRepository subjectRepository, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.subjectRepository = subjectRepository;
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
    @GetMapping("/month/{yearMonth}")
    public ResponseEntity<MonthStatsDTO> getMonthStats(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        double totalDailyReviewRate = 0.0;
        int totalDays = 0;

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            List<ReviewScheduleDTO> dailySchedules = reviewScheduleService.findReviewsForDate(userId, date);

            int dailyTotalReviews = dailySchedules.size();
            if (dailyTotalReviews > 0) {
                int dailyCompletedReviews = (int) dailySchedules.stream()
                        .filter(ReviewScheduleDTO::isReviewed)
                        .count();
                double dailyReviewRate = (double) dailyCompletedReviews / dailyTotalReviews;
                totalDailyReviewRate += dailyReviewRate;
            }
            totalDays++;
        }

        int averageReviewPercentage = totalDays > 0
                ? (int) Math.round((totalDailyReviewRate / totalDays) * 100)
                : 0;

        MonthStatsDTO stats = new MonthStatsDTO(averageReviewPercentage);
        return ResponseEntity.ok(stats);
    }



    // 모든 과목의 복습 통계
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectStatsDTO>> getSubjectStats(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = jwtUtil.getUserIdFromToken(token); // JWT에서 userId 추출

        // 해당 사용자(userId)의 모든 과목 조회
        List<Subject> subjects = subjectRepository.findAllByUserId(userId);

        // Subject 데이터를 SubjectStatsDTO로 변환
        List<SubjectStatsDTO> statsList = subjects.stream()
                .map(subject -> new SubjectStatsDTO(
                        subject.getId(),
                        subject.getSubjectName(),
                        subject.getTotalReviews(),
                        subject.getCompletedReviews(),
                        (int) Math.round(subject.getReviewRate()) // 복습도는 정수로 변환
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(statsList);
    }
}
