package com.sejong.aistudyassistant.quiz.dto;

public record GetReviewQuizRequest (

        Long summaryId,
        Integer dayInterval
){}
