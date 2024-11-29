package com.sejong.aistudyassistant.quiz.dto;

public record GetQuizResultResponse(
        Long userId,
        Long summaryId,
        String date,
        String subjectName,
        int totalQuestions,
        int correctAnswers
){}
