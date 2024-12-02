package com.sejong.aistudyassistant.schedule;

import java.time.LocalDate;


public class ReviewScheduleDTO {
    private Long summaryId;
    private LocalDate createdAt;
    private int dayInterval;
    private String subjectName;
    private String startTime;
    private String endTime;
    private boolean reviewed;


    public ReviewScheduleDTO(Long summaryId, LocalDate createdAt, int dayInterval, String subjectName, String startTime, String endTime, boolean reviewed) {
        this.summaryId = summaryId;
        this.createdAt = createdAt;
        this.dayInterval = dayInterval;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reviewed = reviewed;
    }

    // Getter 메서드 추가
    public Long getSummaryId() {
        return summaryId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public int getDayInterval() {
        return dayInterval;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isReviewed() {
        return reviewed;
    }
}

