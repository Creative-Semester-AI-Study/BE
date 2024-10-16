package com.sejong.aistudyassistant.subject.dto;

public class CreateSubjectRequest {
    private Long subjectId;
    private Long profileId;
    private Long textTransformId;
    private Long summaryId;
    private Long quizId;
    private String subjectName;

    // Getters and Setters
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getTextTransformId() {
        return textTransformId;
    }

    public void setTextTransformId(Long textTransformId) {
        this.textTransformId = textTransformId;
    }

    public Long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
