package com.sejong.aistudyassistant.profile;

import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.login.Repository.UserRepository;
import com.sejong.aistudyassistant.profile.dto.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    // User 정보로 Profile 생성
    public Profile createProfile(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Profile profile = new Profile();
            profile.setUserId(user.getId());
            profile.setStudentId(user.getUserId());
            profile.setStudentName(user.getName());
            // 프로필 이미지 설정 로직이 있다면 추가하세요
            return profileRepository.save(profile);
        } else {
            throw new IllegalArgumentException("User not found with userId: " + userId);
        }
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
