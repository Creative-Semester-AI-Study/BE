package com.sejong.aistudyassistant.profile.dto;

public class ProfileResponse {
    private Long profileId;
    private Long userId;
    private Long studentId;
    private String studentName;

    // 생성자
    public ProfileResponse(Long profileId, Long userId, Long studentId, String studentName) {
        this.profileId = profileId;
        this.userId = userId;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    // Getter 메소드
    public Long getProfileId() {
        return profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }
}
