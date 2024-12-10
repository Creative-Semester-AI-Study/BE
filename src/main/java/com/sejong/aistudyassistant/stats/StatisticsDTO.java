package com.sejong.aistudyassistant.stats;

import com.sejong.aistudyassistant.quiz.dto.GetRecentQuizzesResponse;

import java.util.List;

public record StatisticsDTO (

        List<SubjectStatsDTO> subjectStats,
        GetRecentQuizzesResponse getRecentQuizzes
){}
