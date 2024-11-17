package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record GetReviewQuizResponse(

        Long quizId,
        String question,
        List<String> options

) {}
