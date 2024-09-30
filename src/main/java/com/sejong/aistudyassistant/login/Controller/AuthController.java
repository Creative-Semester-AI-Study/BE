package com.sejong.aistudyassistant.login.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//import sejong_team.matching.SejongProfile.Entity.Profile;
//import sejong_team.matching.SejongProfile.Repository.ProfileRepository;

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

    // ProfileRepository는 주석 처리
    // @Autowired
    // ProfileRepository profileRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/login")
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
                    User findUser = userService.findUser(userId); // user 정보 있나 확인
                    if (findUser == null) {
                        userService.saveUserFromJsonResponse(responseBody, userId); // 없으면 새로 저장
                        findUser = userService.findUser(userId); // 다시 조회하여 findUser에 저장
                    }

                    // 프로필 정보가 있는지 확인하는 부분 주석 처리
                    /*
                    Map<String, Object> profileMap = new HashMap<>();
                    Profile findProfile = profileRepository.findProfileByProfileId(findUser.getId());

                    if (findProfile != null) {
                        profileMap.put("profileId", findProfile.getProfileId());
                        String imagePath = findProfile.getImagePath();
                        profileMap.put("image", imagePath);
                    }
                    */

                    // User 엔티티의 id 필드 포함하여 JSON 응답 생성
                    Map<String, Object> userInfoMap = new HashMap<>();
                    //userInfoMap.put("id", findUser.getId());
                    userInfoMap.put("name", name);
                    userInfoMap.put("department", department);
                    userInfoMap.put("grade", grade);
                    userInfoMap.put("status", status);

                    // profile 관련 부분 주석 처리
                    /*
                    if (!profileMap.isEmpty()) {
                        userInfoMap.put("profile", profileMap);
                    }
                    */

                    String userInfoJson = mapper.writeValueAsString(userInfoMap);
                    return ResponseEntity.ok(userInfoJson);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/getUserInfo")
    public ResponseEntity<String> getuserinfo(@PathVariable String userId) throws JsonProcessingException {
        User findUser = userRepository.findUserById(Long.valueOf(userId));

        Map<String, Object> userInfoMap = new HashMap<>();
//        userInfoMap.put("id", findUser.getId());
//        userInfoMap.put("name", findUser.getName());
//        userInfoMap.put("department", findUser.getDepartment());
//        userInfoMap.put("grade", findUser.getGrade());
//        userInfoMap.put("status", findUser.getStatus());

        // 프로필 정보 주석 처리
        // userInfoMap.put("image", findUser.getProfile().getImagePath());

        String userInfoJson = mapper.writeValueAsString(userInfoMap);
        return ResponseEntity.ok(userInfoJson);
    }
}
