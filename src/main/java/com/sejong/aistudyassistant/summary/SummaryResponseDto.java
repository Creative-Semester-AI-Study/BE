package com.sejong.aistudyassistant.summary;

public class SummaryResponseDto {

    private Long id;
    private String summaryText;
    private Long transcriptId;

    public Long getUserId() {
        return userId;
    }

    private Long userId;

    public SummaryResponseDto(Long userId) {
        this.userId = userId;
    }

    public SummaryResponseDto(Long id, String summaryText, Long transcriptId, Long userId) {
        this.id=id;
        this.summaryText = summaryText;
        this.transcriptId = transcriptId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }
    public String getSummaryText() {
        return summaryText;
    }

    public Long getTranscriptId() {
        return transcriptId;
    }
}
