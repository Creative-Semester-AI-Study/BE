package com.sejong.aistudyassistant.quiz.dto;

public record GetQuizResultRequest(

        Long summaryId,
        Long subjectId,
        Integer dayInterval

){}
