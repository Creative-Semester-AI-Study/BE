package com.sejong.aistudyassistant.mypage;

import com.sejong.aistudyassistant.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyPageService {

    @Autowired
    private MyPageRepository myPageRepository;

    @Autowired
    private ProfileRepository profileRepository; // ProfileRepository 추가

    // MyPage 정보 조회 (userId로)
    public Optional<MyPage> getMyPageByUserId(Long userId) {
        // userId로 profile을 먼저 조회한 후 profileId로 MyPage 조회
        return profileRepository.findByUserId(userId)
                .flatMap(profile -> myPageRepository.findByProfileId(profile.getProfileId()));
    }
}
