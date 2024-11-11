package com.sejong.aistudyassistant.quiz.dto;

public record CreateQuizRequest(
        String lectureText,//요약문 ID가 있으면 굳이 나한테 이걸 줄 필요가 없다 걍 내가 하면 되니까

        Long summaryId,
        Long userId
) {}
