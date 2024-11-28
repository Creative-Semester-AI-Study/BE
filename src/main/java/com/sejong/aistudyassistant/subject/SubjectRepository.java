package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {
    Optional<Subject> findByUserIdAndId(Long userId, Long id);
    List<Subject> findAllByUserId(Long userId);
}

