package com.sejong.aistudyassistant.summary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("SELECT s FROM Summary s WHERE s.userId = :userId AND s.subjectId = :subjectId AND DATE(s.createdAt) = :createdDate")
    Optional<Summary> findByUserIdAndSubjectIdAndCreatedDate(@Param("userId") Long userId,
                                                             @Param("subjectId") Long subjectId,
                                                             @Param("createdDate") LocalDate createdDate);



    List<Summary> findBySubjectId(Long subjectId);
}
