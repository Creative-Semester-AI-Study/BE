package com.sejong.aistudyassistant.summary;

public class SummaryResponseDto {

    private Long id;
    private String summaryText;
    private Long transcriptId;

    public SummaryResponseDto(Long id, String summaryText, Long transcriptId) {
        this.id=id;
        this.summaryText = summaryText;
        this.transcriptId = transcriptId;

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
