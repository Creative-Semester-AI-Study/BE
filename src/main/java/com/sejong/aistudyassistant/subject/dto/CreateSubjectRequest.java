package com.sejong.aistudyassistant.subject.dto;

import jakarta.persistence.Column;

import java.time.LocalTime;

public class CreateSubjectRequest {
    private Long subjectId;
    private Long profileId;
    private String subjectName;
    private String professorName;
    private String days;
    private LocalTime startTime;
    private LocalTime endTime;


    // Getters

    public Long getSubjectId() {
        return subjectId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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
