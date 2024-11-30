package com.sejong.aistudyassistant.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalTime;

@Entity
public class Subject {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false, length = 100)
    private String subjectName;

    @Column(nullable = false, length = 100)
    private String professorName;

    @Column(nullable = false, length = 100)
    private String days;

    @Column(nullable = false, length = 20)
    private LocalTime startTime;

    @Column(nullable = false, length = 20)
    private LocalTime endTime;


    private Long userId;

    // 복습 통계 필드 추가
    private int totalReviews = 0; // 전체 복습 개수
    private int completedReviews = 0; // 완료한 복습 개수
    private double reviewRate = 0.0; // 복습도 (완료한 복습 개수 / 전체 복습 개수)

    // 통계 갱신 메서드
    public void incrementTotalReviews() {
        this.totalReviews++;
        updateReviewRate();
    }

    public void incrementCompletedReviews() {
        this.completedReviews++;
        updateReviewRate();
    }

    public void updateReviewRate() {
        if (totalReviews > 0) {
            this.reviewRate = ((double) completedReviews / totalReviews) * 100;
        } else {
            this.reviewRate = 0.0;
        }
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
// Getters and Setters

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public int getCompletedReviews() {
        return completedReviews;
    }

    public double getReviewRate() {
        return reviewRate;
    }
}
