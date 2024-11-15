package com.sejong.aistudyassistant.quiz.dto;

public record SubmitQuizRequest (

        Long summaryId,
        Long quizId,
        String answer
){}
