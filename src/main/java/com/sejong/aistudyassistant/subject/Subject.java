package com.sejong.aistudyassistant.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subjectId;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private Long textTransformId;

    @Column(nullable = false)
    private Long summaryId;

    @Column(nullable = false)
    private Long quizId;

    @Column(nullable = false, length = 100)
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
