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
public class QuizAttempt { //퀴즈 하나에 대한 정답 여부 저장

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizAttemptId;

    private Long quizId;  // 퀴즈 식별자 (Quiz 테이블과 연결)

    private Long userId;  // 사용자 식별자

    private Long summaryId; //요약문

    private String attemptDate;  // 시도 날짜

    private Boolean correct=false; //답이 맞았는지 안 맞았는지

}
