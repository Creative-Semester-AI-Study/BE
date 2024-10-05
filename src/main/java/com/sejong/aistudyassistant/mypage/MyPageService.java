package com.sejong.aistudyassistant.mypage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyPageService {

    @Autowired
    private MyPageRepository myPageRepository;

    // MyPage 정보 조회 (userId로)
    public Optional<MyPage> getMyPageByUserId(Long userId) {
        return myPageRepository.findByUserId(userId);
    }
}

