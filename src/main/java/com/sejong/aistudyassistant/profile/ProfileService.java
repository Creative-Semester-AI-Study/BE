package com.sejong.aistudyassistant.profile;

import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.login.Repository.UserRepository;
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
    private UserRepository userRepository;

    // User 정보로 Profile 생성
    public Profile createProfile(User user) {
        logger.info("Creating profile for userId: {}", user.getUserId());

        Profile profile = new Profile();
        profile.setUserId(user.getId());
        profile.setStudentId(user.getUserId());
        profile.setStudentName(user.getName());
        // 프로필 이미지 설정 로직이 있다면 추가하세요


        Profile savedProfile = profileRepository.save(profile);
        logger.info("Profile created successfully with ID: {}", savedProfile.getProfileId());
        return savedProfile;
    }


    public Optional<ProfileResponse> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(profile -> new ProfileResponse(
                        profile.getProfileId(),
                        profile.getUserId(),
                        profile.getStudentId(),
                        profile.getStudentName(),
                        profile.getProfileImage()
                ));
    }
}
