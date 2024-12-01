package com.sejong.aistudyassistant.stats;

public class SubjectStatsDTO {
    private Long subjectId;
    private String subjectName;
    private int totalReviews;
    private int completedReviews;
    private int reviewRate;

    public SubjectStatsDTO(Long subjectId, String subjectName, int totalReviews, int completedReviews, int reviewRate) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalReviews = totalReviews;
        this.completedReviews = completedReviews;
        this.reviewRate = reviewRate;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public int getCompletedReviews() {
        return completedReviews;
    }

    public void setCompletedReviews(int completedReviews) {
        this.completedReviews = completedReviews;
    }

    public int getReviewRate() {
        return reviewRate;
    }

    public void setReviewRate(int reviewRate) {
        this.reviewRate = reviewRate;
    }
}
