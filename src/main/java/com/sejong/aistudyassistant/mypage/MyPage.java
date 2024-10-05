package com.sejong.aistudyassistant.mypage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class MyPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myPageId;

    @Column(nullable = false)
    private Long userId;

    // profileId와 subjectId 필드를 추가합니다.
    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private Long subjectId;

    // Getter 및 Setter 추가
    public Long getMyPageId() {
        return myPageId;
    }

    public void setMyPageId(Long myPageId) {
        this.myPageId = myPageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
