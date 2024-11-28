package com.sejong.aistudyassistant.quiz.dto;

import java.util.List;

public record GetRecentQuizzesResponse (

        Long userId,
        String subjectName,
        Integer interval,
        String date,
        GetQuizzesResponse response, //퀴즈 아이디, 질문, 선지 값이 들어가있음
        List<String> chooseAnswers,//사용자가 선택한 답
        Integer totalQuiz,
        Integer correctAnswers
){}
