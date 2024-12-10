package com.sejong.aistudyassistant.summary.dto;

import java.time.LocalDateTime;

public record SelfSummaryCreateRequest(

        String summary,
        Long subjectId,
        LocalDateTime createdDate
){}
