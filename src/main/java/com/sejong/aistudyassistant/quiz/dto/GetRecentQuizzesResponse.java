package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record GetRecentQuizzesResponse (

        Long userId,
        String subjectName,
        Integer round,
        String date,
        List<QuizDetailResponse> quizzes,
        Integer totalQuiz,
        Integer correctAnswers
){}
