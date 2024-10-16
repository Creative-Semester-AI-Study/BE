package com.sejong.aistudyassistant.subject.dto;

public class DeleteSubjectResponse {
    private boolean status;

    public DeleteSubjectResponse(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
