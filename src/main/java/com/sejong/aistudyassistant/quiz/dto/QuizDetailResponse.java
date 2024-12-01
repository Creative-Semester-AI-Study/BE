package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record QuizDetailResponse (
        Long quizId,            // 퀴즈 ID
        String question,        // 질문
        List<String> options,   // 선지
        String chosenAnswer,    // 사용자가 선택한 답
        String correctAnswer    // 실제 정답
){}
