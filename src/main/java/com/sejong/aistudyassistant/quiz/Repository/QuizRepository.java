package com.sejong.aistudyassistant.quiz.Repository;

import com.sejong.aistudyassistant.quiz.Entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {

    List<Quiz> findTop5ByUserIdAndSummaryIdOrderByQuizId(Long userId, Long summaryId);
    List<Quiz> findByUserIdAndSummaryIdAndQuizIdBetween(Long userId, Long summaryId, Long startId, Long endId);
    @Query("SELECT MIN(q.quizId) FROM Quiz q WHERE q.summaryId = :summaryId")
    Long findFirstQuizIdBySummaryId(@Param("summaryId") Long summaryId);

    List<Quiz> findBySummaryId(Long summaryId);
}
