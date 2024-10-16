package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.Profile;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.ModifySubjectResponse;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
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

    @Transactional
    public CreateSubjectResponse createSubject(CreateSubjectRequest request) {
        Subject newSubject = new Subject();

        newSubject.setProfileId(request.getProfileId());
        newSubject.setTextTransformId(request.getTextTransformId());
        newSubject.setSummaryId(request.getSummaryId());
        newSubject.setQuizId(request.getQuizId());
        newSubject.setSubjectName(request.getSubjectName());

        Subject savedSubject = subjectRepository.save(newSubject);

        // profileId로 MyPage 조회
        Optional<MyPage> myPageOptional = myPageRepository.findByProfileId(savedSubject.getProfileId());

        if (myPageOptional.isPresent()) {
            MyPage myPage = myPageOptional.get();

            // 마이페이지가 존재하고, 해당 마이페이지의 subjectId가 null인 경우에만 업데이트
            if (myPage.getSubjectId() == null) {
                myPage.setSubjectId(savedSubject.getSubjectId());  // SubjectId 추가
                myPageRepository.save(myPage);
            }
        } else {
            // 마이페이지가 없거나, subjectId가 null이 아닌 경우 새로운 마이페이지 생성
            MyPage newMyPage = new MyPage();
            newMyPage.setProfileId(savedSubject.getProfileId());  // profileId 설정
            newMyPage.setSubjectId(savedSubject.getSubjectId());
            myPageRepository.save(newMyPage);
        }

        return new CreateSubjectResponse(
                savedSubject.getSubjectId(),
                savedSubject.getProfileId(),
                savedSubject.getTextTransformId(),
                savedSubject.getSummaryId(),
                savedSubject.getQuizId(),
                savedSubject.getSubjectName()
        );
    }

    // 특정 유저의 특정 과목 삭제 (userId를 사용하여)
    @Transactional
    public boolean deleteSubject(Long userId, Long subjectId) {
        // userId로 profile 조회
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        // profileId와 subjectId로 과목 조회 및 삭제
        Subject subject = subjectRepository.findByProfileIdAndSubjectId(profile.getProfileId(), subjectId);
        if (subject != null) {
            subjectRepository.delete(subject);
            return true;  // 삭제 성공 시 true 반환
        } else {
            return false; // 해당 유저의 과목이 없으면 false 반환
        }
    }

    // 특정 유저의 특정 과목 수정 (userId를 사용하여)
    @Transactional
    public ModifySubjectResponse modifySubject(Long userId, Long subjectId, String modifySubjectName) {
        // userId로 profile 조회
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        // profileId와 subjectId로 과목 조회
        Subject modifySubject = subjectRepository.findByProfileIdAndSubjectId(profile.getProfileId(), subjectId);
        if (modifySubject != null) {
            // 과목 정보 수정
            modifySubject.setSubjectName(modifySubjectName);

            // 수정된 과목 저장
            return new ModifySubjectResponse(
                    modifySubject.getSubjectId(),
                    modifySubject.getProfileId(),
                    modifySubject.getTextTransformId(),
                    modifySubject.getSummaryId(),
                    modifySubject.getQuizId(),
                    modifySubject.getSubjectName()
            );
        }
        else {
            throw new RuntimeException("Subject not found for the provided subjectId.");
        }
    }

}
