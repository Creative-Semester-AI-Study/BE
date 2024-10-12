package com.sejong.aistudyassistant.mypage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/study/myPage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // userId로 마이페이지 조회
    @GetMapping("/{userId}")
    public ResponseEntity<MyPageResponse> getMyPageByUserId(@PathVariable Long userId) {
        return myPageService.getMyPageByUserId(userId)
                .map(myPage -> {
                    MyPageResponse response = new MyPageResponse(
                            myPage.getMyPageId(),
                            myPage.getProfileId(),
                            myPage.getSubjectId()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}


