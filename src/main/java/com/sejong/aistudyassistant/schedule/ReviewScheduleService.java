package com.sejong.aistudyassistant.schedule;

import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReviewScheduleService {
    private final TranscriptRepository transcriptRepository;

    public ReviewScheduleService(TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
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
                    .map(transcript -> createReviewScheduleDTO(transcript, dayInterval))
                    .collect(Collectors.toList());
            reviewSchedules.addAll(periodReviews);
        }
        return reviewSchedules;
    }


    private ReviewScheduleDTO createReviewScheduleDTO(Transcript transcript, int dayInterval) {
        String subjectName = transcript.getSubject().getSubjectName();
        String startTime = transcript.getSubject().getStartTime().toString();
        String endTime = transcript.getSubject().getEndTime().toString();
        LocalDate dateOnly = transcript.getCreatedAt().toLocalDate();

        boolean reviewed = checkIfReviewed(transcript);

        return new ReviewScheduleDTO(
                dateOnly,
                dayInterval,
                subjectName,
                startTime,
                endTime
                //reviewed
        );
    }

    private boolean checkIfReviewed(Transcript transcript) {
        // Placeholder method to determine if a transcript has been reviewed
        return false;  // Assume not reviewed for simplicity, implement actual logic
    }
}

