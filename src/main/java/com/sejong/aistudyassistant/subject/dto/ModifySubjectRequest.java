package com.sejong.aistudyassistant.subject.dto;

public class ModifySubjectRequest {
    private Long subjectId;
    private Long profileId;
    private Long textTransformId;
    private Long summaryId;
    private Long quizId;
    private String subjectName;

    // Constructor
    public ModifySubjectRequest(Long subjectId, Long profileId, Long textTransformId, Long summaryId, Long quizId, String subjectName) {
        this.subjectId = subjectId;
        this.profileId = profileId;
        this.textTransformId = textTransformId;
        this.summaryId = summaryId;
        this.quizId = quizId;
        this.subjectName = subjectName;
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
}
