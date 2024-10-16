package com.sejong.aistudyassistant.mypage;

import com.sejong.aistudyassistant.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyPageService {

    @Autowired
    private MyPageRepository myPageRepository;

    @Autowired
    private ProfileRepository profileRepository; // ProfileRepository 추가

    // MyPage 정보 조회 (userId로)
    public Optional<MyPage> getMyPageByUserId(Long userId) {
        // userId로 profile을 먼저 조회한 후 profileId로 MyPage 조회
        return profileRepository.findByUserId(userId)
                .flatMap(profile -> myPageRepository.findByProfileId(profile.getProfileId()));
    }

//    // MyPage에서 과목 삭제 (subjectId를 null로 설정)
//    public boolean deleteSubjectByUserId(Long userId) {
//        // 먼저 userId로 profile을 조회한 후 profileId로 MyPage 조회
//        Optional<MyPage> myPageOptional = getMyPageByUserId(userId);
//
//        if (myPageOptional.isPresent()) {
//            MyPage myPage = myPageOptional.get();
//            myPage.setSubjectId(null);  // subjectId를 null로 설정하여 과목 삭제
//            myPageRepository.save(myPage);
//            return true;  // 삭제 성공 시 true 반환
//        } else {
//            return false; // 해당하는 MyPage가 없을 경우 false 반환
//        }
//    }
}
