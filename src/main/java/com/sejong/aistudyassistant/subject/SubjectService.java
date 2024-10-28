package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.Profile;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.MyPageSubjectSearchResponse;
import com.sejong.aistudyassistant.text.Text;
import com.sejong.aistudyassistant.text.TextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MyPageRepository myPageRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TextRepository textRepository;


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

    public MyPageSubjectSearchResponse getTextsBySubjectId(Long subjectId) {
        List<Text> texts = textRepository.findBySubjectId(subjectId);
        MyPageSubjectSearchResponse response = new MyPageSubjectSearchResponse();
        response.setTextTransforms(texts.stream().map(text -> {
            TextResponse dto = new TextResponse();
            dto.setTextId(text.getId()); // `Text` 엔티티의 ID 필드 사용
            dto.setProfileId(text.getProfileId());
            dto.setSubjectId(text.getSubjectId());
            dto.setTextTitle(text.getTitle());
            dto.setTextContent(text.getContent());
            dto.setTextDate(text.getDate());
            return dto;
        }).collect(Collectors.toList()));
        return response;
    }


}
