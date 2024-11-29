package com.sejong.aistudyassistant.summary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {

    Optional<Summary> findByTranscriptId(Long transcriptId);

    Summary findByUserId(Long userId);

}
