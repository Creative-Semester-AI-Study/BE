package com.sejong.aistudyassistant.mypage;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class MyPageResponse {

    private Long myPageId;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    private LocalDateTime createdAt;
    private Long subjectId;

    // 생성자
    public MyPageResponse(Long myPageId, LocalDateTime createdAt, Long subjectId) {
        this.myPageId = myPageId;
        this.createdAt = createdAt;
        this.subjectId = subjectId;
    }

    // Getter 및 Setter
    public Long getMyPageId() {
        return myPageId;
    }

    public void setMyPageId(Long myPageId) {
        this.myPageId = myPageId;
    }


    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
