package com.sejong.aistudyassistant.stt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    @Override
    Optional<Transcript> findById(Long aLong);

    List<Transcript> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Transcript> findBySubject_Id(Long subjectId);
}
