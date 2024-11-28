package com.sejong.aistudyassistant.profile;

import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.dto.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MyPageRepository myPageRepository;

    // User 정보로 Profile 생성
    public Profile createProfile(User user) {
        logger.info("Creating profile for userId: {}", user.getUserId());

        // 먼저 userId로 프로필이 이미 존재하는지 확인합니다.
        Optional<Profile> existingProfile = profileRepository.findByUserId(user.getId());
        if (existingProfile.isPresent()) {
            logger.info("Profile already exists for userId: {}. Returning existing profile.", user.getUserId());
            return existingProfile.get();
        }

        // 프로필이 존재하지 않는 경우 새로 생성합니다.
        Profile profile = new Profile();
        profile.setUserId(user.getId());
        profile.setStudentId(user.getUserId());
        profile.setStudentName(user.getName());
        // 프로필 이미지 설정 로직이 있다면 추가하세요


        Profile savedProfile = profileRepository.save(profile);
        logger.info("Profile created successfully with ID: {}", savedProfile.getProfileId());

        // 프로필 생성 시 마이페이지에 profile_id만 포함하는 마이페이지 테이블 생성
        MyPage myPage = new MyPage();
        myPage.setProfileId(savedProfile.getProfileId());  // Profile 객체 대신 profileId만 저장

        myPageRepository.save(myPage);

        return savedProfile;
    }

    public Optional<ProfileResponse> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(profile -> new ProfileResponse(
                        profile.getProfileId(),
                        profile.getUserId(),
                        profile.getStudentId(),
                        profile.getStudentName()
                ));
    }
}
