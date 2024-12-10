package com.sejong.aistudyassistant.summary.dto;

public record SelfSummaryCreateRequest(

        String summary,
        Long subjectId
){}
