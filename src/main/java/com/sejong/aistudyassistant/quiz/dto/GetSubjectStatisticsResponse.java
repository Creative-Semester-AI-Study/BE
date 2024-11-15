package com.sejong.aistudyassistant.quiz.dto;

public record GetSubjectStatisticsResponse (

        String subjectName,
        int totalQuestions,
        int correctAnswers

){}
