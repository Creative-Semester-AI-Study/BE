package com.sejong.aistudyassistant.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

    // profileId와 subjectId로 과목 조회
    Optional<Subject> findByUserIdAndId(Long userId, Long id);
    Optional<Subject> findByUserId(Long userId);
}