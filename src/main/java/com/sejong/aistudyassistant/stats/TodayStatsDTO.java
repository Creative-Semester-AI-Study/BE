package com.sejong.aistudyassistant.stats;

public class TodayStatsDTO {
    private int totalReviews;
    private int completedReviews;

    public TodayStatsDTO(int totalReviews, int completedReviews) {
        this.totalReviews = totalReviews;
        this.completedReviews = completedReviews;
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
}