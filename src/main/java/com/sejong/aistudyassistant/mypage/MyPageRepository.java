package com.sejong.aistudyassistant.mypage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyPageRepository extends JpaRepository<MyPage,Long> {
    Optional<MyPage> findByUserId(Long userId);
}
