package com.sejong.aistudyassistant.mypage;

public class MyPageResponse {

    private Long myPageId;
    private Long profileId;
    private Long subjectId;

    // 생성자
    public MyPageResponse(Long myPageId, Long profileId, Long subjectId) {
        this.myPageId = myPageId;
        this.profileId = profileId;
        this.subjectId = subjectId;
    }

    // Getter 및 Setter
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
