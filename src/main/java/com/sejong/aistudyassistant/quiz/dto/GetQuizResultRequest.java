package com.sejong.aistudyassistant.quiz.dto;

public record GetQuizResultRequest(

        Long userId,
        Long summaryId,
        Long subjectId,
        Integer dayInterval

){}
