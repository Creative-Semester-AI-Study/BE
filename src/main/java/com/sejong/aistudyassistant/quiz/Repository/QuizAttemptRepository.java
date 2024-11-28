package com.sejong.aistudyassistant.quiz.Repository;

import com.sejong.aistudyassistant.quiz.Entity.Quiz;
import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt,Long> {

    List<QuizAttempt> findByUserIdAndSummaryIdAndQuizIdBetween(Long userId, Long summaryId, Long startId, Long endId);
    QuizAttempt findByQuizId(Long quizId);
    List<QuizAttempt> findTop5ByOrderByQuizAttemptIdDesc();
}
