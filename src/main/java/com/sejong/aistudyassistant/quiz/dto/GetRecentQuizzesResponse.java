package com.sejong.aistudyassistant.quiz.dto;

import com.sejong.aistudyassistant.quiz.Entity.Quiz;

import java.util.List;

public record GetRecentQuizzesResponse (

        Long userId,
        String subjectName,
        Integer interval,
        String date,
        List<Quiz> quizzes,
        Integer totalQuiz,
        Integer correctAnswers
){}
