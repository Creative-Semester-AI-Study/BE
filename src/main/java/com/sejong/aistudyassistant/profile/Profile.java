package com.sejong.aistudyassistant.profile;

import jakarta.persistence.*;
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
}
