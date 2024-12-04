package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import com.sejong.aistudyassistant.quiz.Repository.QuizAttemptRepository;
import com.sejong.aistudyassistant.stats.SubjectStatsDTO;
import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReviewScheduleService {
    private final TranscriptRepository transcriptRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubjectRepository subjectRepository;

    public ReviewScheduleService(TranscriptRepository transcriptRepository, QuizAttemptRepository quizAttemptRepository, SubjectRepository subjectRepository) {
        this.transcriptRepository = transcriptRepository;
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

            List<Transcript> transcripts = transcriptRepository.findByUserIdAndCreatedAtBetween(userId, targetStartDate, targetEndDate);

            for (Transcript transcript : transcripts) {
                ReviewScheduleDTO dto = createReviewScheduleDTO(userId, transcript, dayInterval, date);
                reviewSchedules.add(dto);
            }
        }
        return reviewSchedules;
    }


    //날짜 date 기준으로 date의 reviewed 여부 조회해서 date의 복습 DTO 생성
    private ReviewScheduleDTO createReviewScheduleDTO(Long userId, Transcript transcript, int dayInterval, LocalDate date) {
        Long summaryId = transcript.getSummaryId();
        String subjectName = transcript.getSubject().getSubjectName();
        String startTime = transcript.getSubject().getStartTime().toString();
        String endTime = transcript.getSubject().getEndTime().toString();
        LocalDate dateOnly = transcript.getCreatedAt().toLocalDate();

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

    /*
    public void updateSubjectStatistics(Long userId, List<ReviewScheduleDTO> schedules) {
        for (ReviewScheduleDTO dto : schedules) {
            // Subject 조회
            Subject subject = subjectRepository.findByUserIdAndId(userId, dto.getSummaryId())
                    .orElse(null);

            if (subject == null) {
                System.err.println("Subject not found for User ID: " + userId + " and Summary ID: " + dto.getSummaryId());
                continue;
            }

            // 통계 갱신
            subject.incrementTotalReviews();
            if (dto.isReviewed()) {
                subject.incrementCompletedReviews();
            }

            // 갱신된 Subject 저장
            subjectRepository.save(subject);
        }
    }
    */
}
