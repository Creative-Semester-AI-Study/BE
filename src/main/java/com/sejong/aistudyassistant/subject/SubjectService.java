package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.Profile;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.ModifySubjectResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MyPageRepository myPageRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    @Transactional
    public CreateSubjectResponse createSubject(CreateSubjectRequest request, Long userId) {
        Subject newSubject = new Subject();

        newSubject.setProfileId(request.getProfileId());
        newSubject.setSubjectName(request.getSubjectName());
        newSubject.setProfessorName(request.getProfessorName());
        newSubject.setDays(request.getDays());
        newSubject.setStartTime(request.getStartTime());
        newSubject.setEndTime(request.getEndTime());
        newSubject.setUserId(userId);

        Subject savedSubject = subjectRepository.save(newSubject);

        // profileId로 MyPage 조회
        Optional<MyPage> myPageOptional = myPageRepository.findByProfileId(savedSubject.getProfileId());

        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();

            // 마이페이지가 존재하고, 해당 마이페이지의 subjectId가 null인 경우에만 업데이트
            if (myPage.getSubjectId() == null) {
                myPage.setSubjectId(savedSubject.getId());  // SubjectId 추가
                myPageRepository.save(myPage);
            }
        } else {
            // 마이페이지가 없거나, subjectId가 null이 아닌 경우 새로운 마이페이지 생성
            MyPage newMyPage = new MyPage();
            newMyPage.setProfileId(savedSubject.getProfileId());  // profileId 설정
            newMyPage.setSubjectId(savedSubject.getId());
            myPageRepository.save(newMyPage);
        }

        return new CreateSubjectResponse(
                savedSubject.getId(),
                savedSubject.getProfileId(),
                savedSubject.getSubjectName(),
                savedSubject.getProfessorName(),
                savedSubject.getDays(),
                savedSubject.getStartTime(),
                savedSubject.getEndTime(),
                savedSubject.getUserId()
        );
    }

    // 특정 유저의 특정 과목 삭제 (userId를 사용하여)
    @Transactional
    public boolean deleteSubject(Long userId, Long subjectId) {
        logger.info("Attempting to delete subject with id {} for user {}", subjectId, userId);

        Subject subject = subjectRepository.findByUserIdAndId(userId, subjectId)
                .orElseThrow(() -> {
                    logger.error("Subject not found for userId: {} and subjectId: {}", userId, subjectId);
                    return new RuntimeException("Subject not found for userId: " + userId + " and subjectId: " + subjectId);
                });

        logger.info("Subject found. Deleting subject with id: {}", subjectId);
        subjectRepository.delete(subject);
        return true;
    }

    // 특정 유저의 특정 과목 수정 (userId를 사용하여)
    @Transactional
    public ModifySubjectResponse modifySubject(Long userId, Long subjectId, String modifySubjectName) {
        logger.info("Attempting to modify subject with id {} for user {}", subjectId, userId);

        Subject subject = subjectRepository.findByUserIdAndId(userId, subjectId)
                .orElseThrow(() -> {
                    logger.error("Subject not found for userId: {} and subjectId: {}", userId, subjectId);
                    return new RuntimeException("Subject not found for userId: " + userId + " and subjectId: " + subjectId);
                });

        subject.setSubjectName(modifySubjectName);
        Subject savedSubject = subjectRepository.save(subject);

        logger.info("Subject modified successfully. New name: {}", modifySubjectName);

        return new ModifySubjectResponse(
                savedSubject.getId(),
                savedSubject.getProfileId(),
                savedSubject.getSubjectName(),
                savedSubject.getProfessorName(),
                savedSubject.getDays(),
                savedSubject.getStartTime(),
                savedSubject.getEndTime(),
                savedSubject.getUserId()
        );
    }

}
