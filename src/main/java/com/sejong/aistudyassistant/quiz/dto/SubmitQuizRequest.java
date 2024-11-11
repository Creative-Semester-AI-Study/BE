package com.sejong.aistudyassistant.quiz.dto;

public record SubmitQuizRequest (

        Long userId,
        Long summaryId,
        Long quizId,
        String answer
){}
