package com.sejong.aistudyassistant.stats;

public class MonthStatsDTO {
    private int monthTotalReviews;
    private int monthCompletedReviews;
    private int monthReviewPercentage;

    public MonthStatsDTO(int monthTotalReviews, int monthCompletedReviews, int monthReviewPercentage) {
        this.monthTotalReviews = monthTotalReviews;
        this.monthCompletedReviews = monthCompletedReviews;
        this.monthReviewPercentage = monthReviewPercentage;
    }

    public int getMonthTotalReviews() {
        return monthTotalReviews;
    }

    public void setMonthTotalReviews(int monthTotalReviews) {
        this.monthTotalReviews = monthTotalReviews;
    }

    public int getMonthCompletedReviews() {
        return monthCompletedReviews;
    }

    public void setMonthCompletedReviews(int monthCompletedReviews) {
        this.monthCompletedReviews = monthCompletedReviews;
    }

    public int getMonthReviewPercentage() {
        return monthReviewPercentage;
    }

    public void setMonthReviewPercentage(int monthReviewPercentage) {
        this.monthReviewPercentage = monthReviewPercentage;
    }
}