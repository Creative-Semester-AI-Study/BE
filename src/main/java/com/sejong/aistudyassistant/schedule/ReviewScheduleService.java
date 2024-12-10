package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import com.sejong.aistudyassistant.quiz.Repository.QuizAttemptRepository;
import com.sejong.aistudyassistant.stats.SubjectStatsDTO;
import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import com.sejong.aistudyassistant.summary.Summary;
import com.sejong.aistudyassistant.summary.SummaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ReviewScheduleService {
    private final SummaryRepository summaryRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubjectRepository subjectRepository;

    public ReviewScheduleService(SummaryRepository summaryRepository, QuizAttemptRepository quizAttemptRepository, SubjectRepository subjectRepository) {
        this.summaryRepository = summaryRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<ReviewScheduleDTO> findReviewsForDate(Long userId, LocalDate date) {
        List<ReviewScheduleDTO> reviewSchedules = new ArrayList<>();
        int[] dayIntervals = {1, 3, 7, 15, 30};

        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDate = date.atTime(23, 59, 59);

        for (int dayInterval : dayIntervals) {
            LocalDateTime targetStartDate = startOfDate.minusDays(dayInterval);
            LocalDateTime targetEndDate = endOfDate.minusDays(dayInterval);

            List<Summary> summarys = summaryRepository.findByUserIdAndCreatedAtBetween(userId, targetStartDate, targetEndDate);

            for (Summary summary : summarys) {
                ReviewScheduleDTO dto = createReviewScheduleDTO(userId, summary, dayInterval, date);
                reviewSchedules.add(dto);
            }
        }
        return reviewSchedules;
    }


    //날짜 date 기준으로 date의 reviewed 여부 조회해서 date의 복습 DTO 생성
    private ReviewScheduleDTO createReviewScheduleDTO(Long userId, Summary summary, int dayInterval, LocalDate date) {
        Long summaryId = summary.getId();
        String subjectName = summary.getSubject().getSubjectName();
        String startTime = summary.getSubject().getStartTime().toString();
        String endTime = summary.getSubject().getEndTime().toString();
        LocalDate dateOnly = summary.getCreatedAt().toLocalDate();

        boolean reviewed = checkIfReviewed(userId, summaryId, date, dayInterval);

        return new ReviewScheduleDTO(
                summaryId,
                dateOnly,
                dayInterval,
                subjectName,
                startTime,
                endTime,
                reviewed
        );
    }

    public boolean checkIfReviewed(Long userId, Long summaryId, LocalDate date, int dayInterval) {
        // userId와 summaryId를 모두 사용해 QuizAttempt 조회
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdAndSummaryId(userId, summaryId);

        // dayIntervals 배열 설정
        int[] dayIntervals = {1, 3, 7, 15, 30};

        // 현재 dayInterval의 index를 찾음
        int currentIndex = -1;
        for (int i = 0; i < dayIntervals.length; i++) {
            if (dayIntervals[i] == dayInterval) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Invalid dayInterval: " + dayInterval);
        }

        // 이전 간격 계산
        int previousInterval = currentIndex == 0 ? 0 : dayIntervals[currentIndex - 1];

        // targetDate 범위 설정
        LocalDate targetStartDate = date.minusDays(
                dayIntervals[currentIndex] - previousInterval); // 시작 범위
        LocalDate targetEndDate = date; // 종료 범위 (date 포함)

        // targetDate 범위 내에서 복습 여부 확인
        return attempts.stream()
                .anyMatch(attempt -> {
                    LocalDate attemptDate = LocalDate.parse(attempt.getAttemptDate());
                    return attemptDate.isAfter(targetStartDate)
                            && attemptDate.isBefore(targetEndDate.plusDays(1));
                });
    }

    public List<SubjectStatsDTO> updateAndGetSubjectStats(Long userId) {
        LocalDate today = LocalDate.now();
        int[] dayIntervals = {1, 3, 7, 15, 30};

        // 오늘 이전의 특정 사용자의 모든 Transcript 조회
        List<Summary> summarys = summaryRepository.findByUserIdAndCreatedAtBefore(userId, today.atStartOfDay());

        // Subject별 통계 계산
        Map<Long, SubjectStatsDTO> subjectStatsMap = new HashMap<>();

        for (Summary summary : summarys) {
            Subject subject = summary.getSubject(); // Subject 참조를 통해 가져옴
            if (subject == null) {
                System.err.println("Subject not found for Transcript ID: " + summary.getId());
                continue;
            }
            Long subjectId = subject.getId();

            for (int dayInterval : dayIntervals) {
                LocalDate targetDate = summary.getCreatedAt().toLocalDate().plusDays(dayInterval);

                if (!targetDate.isAfter(today)) { // createAt + dayInterval <= 오늘
                    boolean reviewed = checkIfReviewed(userId, summary.getId(), targetDate, dayInterval);

                    // SubjectStatsDTO 초기화 또는 업데이트
                    subjectStatsMap.putIfAbsent(subjectId, new SubjectStatsDTO(
                            subjectId,
                            subject.getSubjectName(),
                            0, // totalReviews
                            0, // completedReviews
                            0  // reviewRate
                    ));

                    SubjectStatsDTO stats = subjectStatsMap.get(subjectId);
                    stats.setTotalReviews(stats.getTotalReviews() + 1);
                    if (reviewed) {
                        stats.setCompletedReviews(stats.getCompletedReviews() + 1);
                    }
                }
            }
        }

        // ReviewRate 계산
        for (SubjectStatsDTO stats : subjectStatsMap.values()) {
            int totalReviews = stats.getTotalReviews();
            int completedReviews = stats.getCompletedReviews();
            stats.setReviewRate(totalReviews > 0 ? (int) Math.round((completedReviews / (double) totalReviews) * 100) : 0);
        }

        // DTO 리스트 반환
        return new ArrayList<>(subjectStatsMap.values());
    }
}

