package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record GetLearningQuizResponse (

        Long quizId,
        String question,
        List<String> options

){}
