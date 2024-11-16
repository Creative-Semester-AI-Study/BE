package com.sejong.aistudyassistant.mypage;

import com.sejong.aistudyassistant.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyPageRepository extends JpaRepository<MyPage,Long> {
    // userId가 MyPage에 직접 존재하지 않으므로 Profile을 통해 조회
    //Optional<MyPage> findByProfile_UserId(Long userId);  // Profile의 userId를 통해 MyPage 조회

    // profileId로 MyPage 조회
    Optional<MyPage> findByProfileId(Long profileId);
    Optional<MyPage> findBySubjectId(Long subjectId);

}
