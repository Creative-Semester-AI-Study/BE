package com.sejong.aistudyassistant.mypage;

import jakarta.persistence.*;

@Entity
public class MyPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myPageId;

    // profileId를 직접 저장
    @Column(nullable = false)
    private Long profileId;

    // 기타 필드들...
    private Long subjectId;

    // Getters and Setters
    public Long getMyPageId() {
        return myPageId;
    }

    public void setMyPageId(Long myPageId) {
        this.myPageId = myPageId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
