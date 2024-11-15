package com.sejong.aistudyassistant.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

    // profileId와 subjectId로 과목 조회
    Subject findByProfileIdAndSubjectId(Long profileId, Long subjectId);

    Subject findBySummaryId(Long summaryId);
    List<Subject> findByProfileId(Long profileId);
}

