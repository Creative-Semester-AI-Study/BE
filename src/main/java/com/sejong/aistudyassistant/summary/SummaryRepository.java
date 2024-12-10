package com.sejong.aistudyassistant.summary;

import com.sejong.aistudyassistant.stt.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {

    Optional<Summary> findByTranscriptId(Long transcriptId);

    Summary findByUserId(Long userId);

    boolean existsBySubjectId(Long subjectId);

    List<Summary> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Summary> findByUserIdAndCreatedAtBefore(Long userId, LocalDateTime dateTime);
}
