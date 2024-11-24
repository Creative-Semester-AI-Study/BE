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

    //복습 여부 구하기 위해 조회
    List<QuizAttempt> findByUserIdAndSummaryId(Long userId, Long summaryId); // summaryId로 QuizAttempt 조회
}
