package com.sejong.aistudyassistant.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String studentName;

    @Column
    private byte[] profileImage;

    // 필요한 경우 추가적인 생성자 또는 메서드를 정의할 수 있습니다.
}
