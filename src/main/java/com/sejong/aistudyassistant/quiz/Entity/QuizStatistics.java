package com.sejong.aistudyassistant.quiz.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizStatisticsId;

    private Long userId;

    private Long summaryId;

    private Long subjectId;

    private int totalQuestions=20;

    private int correctAnswers=0;
}
