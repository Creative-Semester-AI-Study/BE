package com.sejong.aistudyassistant.stt;

import java.time.LocalDateTime;

public class TranscriptDTO {
    private Long id;

    public TranscriptDTO(Long id, Long subjectId, String audioFileName, String transcriptText, LocalDateTime createdAt, Long userId, Long summaryId, Long quizId) {
        this.id = id;
        this.subjectId = subjectId;
        this.audioFileName = audioFileName;
        this.transcriptText = transcriptText;
        this.createdAt = createdAt;
        this.userId=userId;
        this.summaryId=summaryId;
        this.quizId=quizId;
    }

    private Long subjectId;
    private String audioFileName;
    private String transcriptText;
    private LocalDateTime createdAt;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }

    private Long quizId;

    private Long summaryId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public void setTranscriptText(String transcriptText) {
        this.transcriptText = transcriptText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
