package com.sejong.aistudyassistant.quiz.dto;

public record SubmitQuizResponse (

        String correctAnswers,
        String choiceAnswers,
        boolean correct

) {}
