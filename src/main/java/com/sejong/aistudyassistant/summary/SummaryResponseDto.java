package com.sejong.aistudyassistant.summary;

public class SummaryResponseDto {
    private String summaryText;
    private Long transcriptId;

    public SummaryResponseDto(String summaryText, Long transcriptId) {
        this.summaryText = summaryText;
        this.transcriptId = transcriptId;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public Long getTranscriptId() {
        return transcriptId;
    }
}
