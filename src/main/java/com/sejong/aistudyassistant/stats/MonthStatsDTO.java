package com.sejong.aistudyassistant.stats;

public class MonthStatsDTO {
    private int monthReviewPercentage;

    public MonthStatsDTO(int monthReviewPercentage) {
        this.monthReviewPercentage = monthReviewPercentage;
    }

    public int getMonthReviewPercentage() {
        return monthReviewPercentage;
    }

    public void setMonthReviewPercentage(int monthReviewPercentage) {
        this.monthReviewPercentage = monthReviewPercentage;
    }
}