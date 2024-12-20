package com.sejong.aistudyassistant.subject.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class NextSubjectResponse {
    private Long subjectId;
    private Long profileId;
    private String subjectName;
    private String days;
    private String professorName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String learningStatus;

    private Long userId;

    public Long getUserId() {
        return userId;
    }
    public NextSubjectResponse(Long subjectId, Long profileId, String subjectName,
                               String professorName, String days, LocalTime startTime, LocalTime endTime, Long userId, String learningStatus) {
        this.subjectId = subjectId;
        this.profileId = profileId;
        this.subjectName = subjectName;
        this.professorName = professorName;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId=userId;
        this.learningStatus = learningStatus;
    }

    // Getters
    public Long getSubjectId() {
        return subjectId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getDays() {
        return days;
    }
}
