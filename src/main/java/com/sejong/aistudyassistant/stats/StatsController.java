package com.sejong.aistudyassistant.stats;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.quiz.QuizService;
import com.sejong.aistudyassistant.quiz.dto.GetRecentQuizzesResponse;
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
    private final QuizService quizService;
    private final JwtUtil jwtUtil;

    public StatsController(QuizService quizService,ReviewScheduleService reviewScheduleService,SubjectRepository subjectRepository, JwtUtil jwtUtil) {
        this.reviewScheduleService = reviewScheduleService;
        this.quizService=quizService;
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

                // 리뷰가 있는 경우에만 totalDays 증가
                totalDays++;
            }
        }

        int averageReviewPercentage = totalDays > 0
                ? (int) Math.round((totalDailyReviewRate / totalDays) * 100)
                : 0;

        MonthStatsDTO stats = new MonthStatsDTO(averageReviewPercentage);
        return ResponseEntity.ok(stats);
    }


    // 모든 과목의 복습 통계 갱신 및 반환
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectStatsDTO>> updateAndGetSubjectStats(@RequestHeader("Authorization") String authHeader) {
        // JWT 토큰에서 사용자 ID 추출
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 과목 통계 갱신 및 가져오기
        List<SubjectStatsDTO> subjectStats = reviewScheduleService.updateAndGetSubjectStats(userId);

        // 응답 반환
        return ResponseEntity.ok(subjectStats);
    }
}

