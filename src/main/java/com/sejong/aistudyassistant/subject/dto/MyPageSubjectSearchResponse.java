package com.sejong.aistudyassistant.subject.dto;

import com.sejong.aistudyassistant.text.Text;

import java.time.LocalDateTime;
import java.util.List;

public class MyPageSubjectSearchResponse {
    private boolean status;
    private List<Text> textResponses; // 텍스트 데이터를 담을 리스트 추가

    // 기본 생성자
    public MyPageSubjectSearchResponse() {
    }

    // 상태와 텍스트 리스트를 초기화하는 생성자
    public MyPageSubjectSearchResponse(boolean status, List<Text> textResponses) {
        this.status = status;
        this.textResponses = textResponses;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Text> getTextResponses() {
        return textResponses;
    }

    public void setTextResponses(List<Text> textResponses) {
        this.textResponses = textResponses;
    }

    public void setTextTransforms(List<Object> collect) {
    }
}
