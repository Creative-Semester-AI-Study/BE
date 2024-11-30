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

        // 하루 전체를 커버하기 위해 LocalDate를 LocalDateTime으로 변환
        LocalDateTime startOfDate = date.atStartOfDay(); // 해당 날짜의 시작 시간 (00:00)
        LocalDateTime endOfDate = date.atTime(23, 59, 59); // 해당 날짜의 끝 시간 (23:59)

        for (int dayInterval : dayIntervals) {
            // 타겟 날짜의 시작과 끝 시간을 계산
            LocalDateTime targetStartDate = startOfDate.minusDays(dayInterval);
            LocalDateTime targetEndDate = endOfDate.minusDays(dayInterval);

            // 리포지토리 메서드 업데이트 필요: findByUserIdAndCreatedAtBetween 사용
            List<Transcript> transcripts = transcriptRepository.findByUserIdAndCreatedAtBetween(userId, targetStartDate, targetEndDate);
            List<ReviewScheduleDTO> periodReviews = transcripts.stream()
                    .map(transcript -> createReviewScheduleDTO(userId, transcript, dayInterval, date))
                    .collect(Collectors.toList());
            reviewSchedules.addAll(periodReviews);
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


    public List<SubjectStatsDTO> getAllSubjectsStats(Long userId) {
        // 모든 과목(subject) 조회
        List<Subject> subjects = subjectRepository.findAllByUserId(userId);

        List<SubjectStatsDTO> subjectStatsList = new ArrayList<>();
        for (Subject subject : subjects) {
            // 특정 과목의 복습 스케줄 조회
            List<ReviewScheduleDTO> reviewSchedules = findReviewsForSubject(userId, subject.getId());
            // 복습 완료 수와 총 복습 스케줄 수를 계산
            long reviewedCount = reviewSchedules.stream().filter(ReviewScheduleDTO::isReviewed).count();
            int totalReviews = reviewSchedules.size();
            int reviewPercentage = totalReviews > 0 ? (int) ((reviewedCount * 100) / totalReviews) : 0;

            // 과목별 복습도 DTO 생성
            SubjectStatsDTO subjectStatsDTO = new SubjectStatsDTO(
                    subject.getId(),
                    subject.getSubjectName(),
                    totalReviews,
                    (int) reviewedCount,
                    reviewPercentage
            );
            subjectStatsList.add(subjectStatsDTO);
        }
        return subjectStatsList;
    }


}

