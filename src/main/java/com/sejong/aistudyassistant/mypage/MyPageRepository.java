package com.sejong.aistudyassistant.mypage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyPageRepository extends JpaRepository<MyPage,Long> {
}
