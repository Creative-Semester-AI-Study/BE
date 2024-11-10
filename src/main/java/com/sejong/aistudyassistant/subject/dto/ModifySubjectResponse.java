package com.sejong.aistudyassistant.subject.dto;

import java.time.LocalTime;

public class ModifySubjectResponse{
    private Long subjectId;
    private Long profileId;
    private Long textTransformId;
    private Long summaryId;
    private Long quizId;
    private String subjectName;
    private String days;
    private String professorName;
    private LocalTime startTime;
    private LocalTime endTime;

    // Constructor
    public ModifySubjectResponse(Long subjectId, Long profileId, Long textTransformId,
                                 Long summaryId, Long quizId, String subjectName,
                                 String professorName, String days, LocalTime startTime, LocalTime endTime) {
        this.subjectId = subjectId;
        this.profileId = profileId;
        this.textTransformId = textTransformId;
        this.summaryId = summaryId;
        this.quizId = quizId;
        this.subjectName = subjectName;
        this.professorName = professorName;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public Long getSubjectId() {
        return subjectId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public Long getTextTransformId() {
        return textTransformId;
    }

    public Long getSummaryId() {
        return summaryId;
    }

    public Long getQuizId() {
        return quizId;
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
