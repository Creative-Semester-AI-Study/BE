package com.sejong.aistudyassistant.quiz.Repository;

import com.sejong.aistudyassistant.quiz.Entity.QuizStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizStatisticsRepository extends JpaRepository<QuizStatistics, Long> {

    Optional<QuizStatistics> findByUserIdAndSummaryId(Long userId, Long summaryId);
    List<QuizStatistics> findBySubjectId(Long subjectId);
}
