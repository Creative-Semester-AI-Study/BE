package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.ModifySubjectResponse;

import com.sejong.aistudyassistant.subject.dto.TargetDaySubestsResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

    //오늘의 과목 조회(userId를 사용하여)
    @Transactional
    public List<TargetDaySubestsResponse> getSubjectsByUserIdAndDate(Long userId, Date date) {
        logger.info("Retrieving subjects for user {} on date {}", userId, date);

        // Date를 LocalDate로 변환
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek targetDayEnglish = localDate.getDayOfWeek();

        // 유저의 특정 요일 과목 조회
        List<Subject> allSubjects = getAllSubjectsByUserId(userId);
        String targetDayKorean = convertDayOfWeekToKorean(targetDayEnglish);
        List<Subject> targetDaySubjects = new ArrayList<>();

        for (Subject subject : allSubjects) {
            if(subject.getDays().contains(targetDayKorean)){
                targetDaySubjects.add(subject);
            }
        }

        if (targetDaySubjects.isEmpty()) {
            logger.warn("No subjects found for user {} on {}", userId, targetDayKorean);
            throw new RuntimeException("No subjects found for user " + userId + " on " + targetDayKorean);
        }

        //특정 요일 과목 정렬
        List<Subject> sortedSubjects = targetDaySubjects.stream()
                .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                .collect(Collectors.toList());

        logger.info("{} subjects found for user {} on {}", targetDaySubjects.size(), userId, targetDayKorean);

        List<TargetDaySubestsResponse> response = sortedSubjects.stream().map(subject -> {
            TargetDaySubestsResponse dto = new TargetDaySubestsResponse(
                    subject.getId(),
                    subject.getProfileId(),
                    subject.getSubjectName(),
                    subject.getProfessorName(),
                    subject.getDays(),
                    subject.getStartTime(),
                    subject.getEndTime(),
                    subject.getUserId());
            return dto;
        }).collect(Collectors.toList());

        return response;
    }

    //특정 유저의 모든 과목 조회(userId를 사용하여)
    @Transactional
    public List<Subject> getAllSubjectsByUserId(Long userId) {
        logger.info("Attempting to retrieve all subjects for user {}", userId);

        List<Subject> subjects = subjectRepository.findAllByUserId(userId);

        if (subjects.isEmpty()) {
            logger.warn("No subjects found for userId: {}", userId);
            throw new RuntimeException("No subjects found for userId: " + userId);
        }

        logger.info("{} subjects found for user {}", subjects.size(), userId);
        return subjects;
    }

    public static String convertDayOfWeekToKorean(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                throw new IllegalArgumentException("잘못된 요일 값입니다: " + dayOfWeek);
        }
    }
}
