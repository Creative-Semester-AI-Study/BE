package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import com.sejong.aistudyassistant.quiz.Repository.QuizAttemptRepository;
import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
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

    public ReviewScheduleService(TranscriptRepository transcriptRepository, QuizAttemptRepository quizAttemptRepository) {
        this.transcriptRepository = transcriptRepository;
        this.quizAttemptRepository = quizAttemptRepository;
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
                    .map(transcript -> createReviewScheduleDTO(transcript, dayInterval, date))
                    .collect(Collectors.toList());
            reviewSchedules.addAll(periodReviews);
        }
        return reviewSchedules;
    }


    private ReviewScheduleDTO createReviewScheduleDTO(Transcript transcript, int dayInterval, LocalDate date) {
        Long summaryId = transcript.getSummaryId();
        String subjectName = transcript.getSubject().getSubjectName();
        String startTime = transcript.getSubject().getStartTime().toString();
        String endTime = transcript.getSubject().getEndTime().toString();
        LocalDate dateOnly = transcript.getCreatedAt().toLocalDate();

        boolean reviewed = checkIfReviewed(summaryId, date);

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

    public boolean checkIfReviewed(Long summaryId, LocalDate date) {
        // summaryId를 이용해 quizAttemptRepository에서 데이터를 가져옴
        List<QuizAttempt> attempts = quizAttemptRepository.findBySummaryId(summaryId);

        // 주어진 date와 attemptDate가 같은 경우가 있으면 복습 완료(true)
        return attempts.stream()
                .anyMatch(attempt -> LocalDate.parse(attempt.getAttemptDate()).isEqual(date));
    }
}

