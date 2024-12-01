package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record GetQuizzesResponse(

        Long quizId,
        String question,
        List<String> options

) {}
