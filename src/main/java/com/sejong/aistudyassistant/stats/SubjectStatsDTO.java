package com.sejong.aistudyassistant.stats;

public class SubjectStatsDTO {
    private Long subjectId;
    private String subjectName;
    private int totalReviews;
    private int reviewedCount;
    private int reviewPercentage;

    public SubjectStatsDTO(Long subjectId, String subjectName, int totalReviews, int reviewedCount, int reviewPercentage) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalReviews = totalReviews;
        this.reviewedCount = reviewedCount;
        this.reviewPercentage = reviewPercentage;
    }

    // Getters and Setters
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

    public int getReviewedCount() {
        return reviewedCount;
    }

    public void setReviewedCount(int reviewedCount) {
        this.reviewedCount = reviewedCount;
    }

    public int getReviewPercentage() {
        return reviewPercentage;
    }

    public void setReviewPercentage(int reviewPercentage) {
        this.reviewPercentage = reviewPercentage;
    }
}
