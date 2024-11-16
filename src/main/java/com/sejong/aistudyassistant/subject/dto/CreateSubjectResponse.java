package com.sejong.aistudyassistant.subject.dto;

import java.time.LocalTime;

public class CreateSubjectResponse {
    private Long subjectId;
    private Long profileId;
    private String subjectName;
    private String professorName;
    private String days;
    private LocalTime startTime;
    private LocalTime endTime;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    // Constructor
    public CreateSubjectResponse(Long subjectId, Long profileId, String subjectName,
                                 String professorName, String days, LocalTime startTime, LocalTime endTime, Long userId) {
        this.subjectId = subjectId;
        this.profileId = profileId;
        this.subjectName = subjectName;
        this.professorName = professorName;
        this.days =days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId=userId;
    }

    // Getters
    public Long getProfileId() {
        return profileId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public String getDays() {
        return days;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
