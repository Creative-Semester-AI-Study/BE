package com.sejong.aistudyassistant.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

    Optional<Subject> findByUserIdAndId(Long userId, Long id);
    Optional<Subject> findByUserId(Long userId);

    // 특정 사용자(userId)가 등록한 모든 과목을 조회
    List<Subject> findAllByUserId(Long userId);
}

