package com.sejong.aistudyassistant.login.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.login.Repository.UserRepository;
//import sejong_team.matching.SejongProfile.Entity.Profile;
//import sejong_team.matching.SejongProfile.Repository.ProfileRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    private final ProfileRepository profileRepository;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void saveUserFromJsonResponse(String responseBody, Long userId) {
        boolean isUserExists = userRepository.existsById(userId); // spring data jpa 쿼리 메서드 existsByUserId
        if (isUserExists) {
            return;
        }
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            String name = jsonResponse.get("result").get("body").get("name").asText();
            String major = jsonResponse.get("result").get("body").get("major").asText();
            String grade = jsonResponse.get("result").get("body").get("grade").asText();
            String status = jsonResponse.get("result").get("body").get("status").asText();

            Profile profile = new Profile();
            profile.setImageName("basic");
            profile.setImagePath("http://172.16.86.241:8080/image/basic.jpg");
            profileRepository.save(profile);
            User user = new User(userId, name, major, status, grade, profile); // user entity 생성
            System.out.println("ProfileId: " + user.getProfile().getProfileId());
            userRepository.save(user);
            profile.setUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("사용자 정보 저장 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public User findUser(Long userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        return userOptional.orElse(null);
    }
}