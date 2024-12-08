package com.sejong.aistudyassistant.login.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.profile.ProfileService;
import com.sejong.aistudyassistant.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.sejong.aistudyassistant.login.Entity.User;
import com.sejong.aistudyassistant.login.Repository.UserRepository;
import com.sejong.aistudyassistant.login.RequestDto.AuthRequestDto;
import com.sejong.aistudyassistant.login.Service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private static final String apiUrl = "https://auth.imsejong.com/auth?method=ClassicSession";
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final ObjectMapper mapper;
    ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;
    private final ProfileService profileService;

    @PostMapping("/study/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDto request) {
        String id = request.getId();
        String pw = request.getPw();
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", id);
        multiValueMap.add("pw", pw);
        log.info("multivaluemap = {}", multiValueMap);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, request, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                JsonNode jsonNode = mapper.readTree(responseBody);
                boolean isAuthenticated = jsonNode.get("result").get("is_auth").asBoolean();

                if (isAuthenticated) {
                    // 사용자 정보 추출
                    JsonNode bodyNode = jsonNode.get("result").get("body");
                    String name = bodyNode.get("name").asText();
                    String department = bodyNode.get("major").asText();
                    String grade = bodyNode.get("grade").asText();
                    String status = bodyNode.get("status").asText();

                    Long userId = Long.parseLong(id);
                    User findUser = userService.findUser(userId);

                    // Log to check if findUser has been retrieved
                    if (findUser != null) {
                        log.info("User found: {}", findUser);
                    } else {
                        log.info("User not found. Saving new user from response.");
                        userService.saveUserFromJsonResponse(responseBody, userId);
                        findUser = userService.findUser(userId); // 저장 후 다시 조회
                        if (findUser == null) {
                            log.error("Failed to retrieve user after saving. Aborting operation.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                        log.info("User created and retrieved: {}", findUser);

                        // Profile 생성 (User가 저장된 이후에 실행)
                        profileService.createProfile(findUser);
                    }

                    // JWT 토큰 생성
                    String token = jwtUtil.generateToken(userId);
                    log.info("Generated Token: {}", token);

                    // User 엔티티의 정보와 함께 JSON 응답 생성
                    Map<String, Object> userInfoMap = new HashMap<>();
                    userInfoMap.put("token", token); // 토큰 추가
                    userInfoMap.put("id", findUser.getId());
                    userInfoMap.put("name", name);
                    userInfoMap.put("department", department);
                    userInfoMap.put("grade", grade);
                    userInfoMap.put("status", status);

                    // Profile 정보 추가 (생성된 프로필 조회)
                    ProfileResponse profile = profileService.getProfileByUserId(findUser.getUserId()).orElse(null);
                    if (profile != null) {
                        userInfoMap.put("profile", profile);
                    }

                    String userInfoJson = mapper.writeValueAsString(userInfoMap);
                    return ResponseEntity.ok(userInfoJson);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error("An error occurred during login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
