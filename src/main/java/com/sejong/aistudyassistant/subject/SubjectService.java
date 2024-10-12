package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MyPageRepository myPageRepository;

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
}
