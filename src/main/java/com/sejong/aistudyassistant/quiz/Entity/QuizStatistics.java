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
public class QuizStatistics { //하나의 요약문에 대한 통계치

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizStatisticsId;
    private Long userId;    //유저
    private Long summaryId; //요약문
    private Long subjectId; //과목
    private int totalQuestions=20;  // 총 문제 수(각 요약문마다 20개 이므로 20으로 전부 설정)
    private int correctAnswers=0;  // 맞힌 문제 수
}
