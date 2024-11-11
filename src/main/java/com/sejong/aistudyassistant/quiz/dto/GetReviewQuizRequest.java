package com.sejong.aistudyassistant.quiz.dto;

public record GetReviewQuizRequest (

        Long userId,
        Long summaryId,
        Integer dayInterval
){}
