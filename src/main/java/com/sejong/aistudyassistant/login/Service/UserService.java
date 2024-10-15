package com.sejong.aistudyassistant.login.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.aistudyassistant.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.login.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ProfileService profileService;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, ProfileService profileService) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.profileService = profileService;
    }

    @Transactional
    public void saveUserFromJsonResponse(String responseBody, Long Id) {
        boolean isUserExists = userRepository.existsById(Id);
        if (isUserExists) {
            return;
        }
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            String name = jsonResponse.get("result").get("body").get("name").asText();
            String major = jsonResponse.get("result").get("body").get("major").asText();
            String grade = jsonResponse.get("result").get("body").get("grade").asText();
            String status = jsonResponse.get("result").get("body").get("status").asText();

            // User 엔티티 생성 및 저장
            User user = new User(Id, name, major, status, grade);
            userRepository.save(user);

            // Profile 생성
            logger.info("Calling createProfile for userId: {}", Id);
            profileService.createProfile(user);

        } catch (Exception e) {
            logger.error("Error saving user and creating profile: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 정보 저장 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public User findUser(Long userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        return userOptional.orElse(null);
    }
}
