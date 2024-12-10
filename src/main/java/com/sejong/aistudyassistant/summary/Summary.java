package com.sejong.aistudyassistant.summary;

import com.sejong.aistudyassistant.subject.Subject;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transcriptId;

    @Column(columnDefinition = "TEXT")
    private String summaryText;

    private Long userId;

    private Long subjectId; // subjectId 필드 유지

    @ManyToOne
    @JoinColumn(name = "subjectId", insertable = false, updatable = false) // 연관 관계 설정
    private Subject subject; // Subject 엔티티 매핑

    private LocalDateTime createdAt;

    // Getter와 Setter 추가
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(Long transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
