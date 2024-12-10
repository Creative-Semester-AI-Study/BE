package com.sejong.aistudyassistant.summary.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetSummaryRequest(

        LocalDate date,
        Long subjectId
){}
