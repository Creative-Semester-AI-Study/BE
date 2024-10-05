package com.sejong.aistudyassistant.profile;

import com.sejong.aistudyassistant.profile.dto.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

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
