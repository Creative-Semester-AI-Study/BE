package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.mypage.MyPageRepository;
import com.sejong.aistudyassistant.profile.ProfileRepository;

import com.sejong.aistudyassistant.subject.dto.*;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    //특정 과목 조회기능
    @Transactional
    public CheckSubjectResponse checkSubject(Long userId, Long subjectId) {
        logger.info("Attempting to modify subject with id {} for user {}", subjectId, userId);

        Subject subject = subjectRepository.findByUserIdAndId(userId, subjectId)
                .orElseThrow(() -> {
                    logger.error("Subject not found for userId: {} and subjectId: {}", userId, subjectId);
                    return new RuntimeException("Subject not found for userId: " + userId + " and subjectId: " + subjectId);
                });


        return new CheckSubjectResponse(
                subject.getId(),
                subject.getProfileId(),
                subject.getSubjectName(),
                subject.getProfessorName(),
                subject.getDays(),
                subject.getStartTime(),
                subject.getEndTime(),
                subject.getUserId()
        );
    }

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
    public List<TargetDaySubjectResponse> getSubjectsByUserIdAndDate(Long userId, LocalDate localDate) {
        logger.info("Retrieving subjects for user {} on date {}", userId, localDate);

        // 유저의 특정 요일 과목 조회
        DayOfWeek targetDayEnglish = localDate.getDayOfWeek();
        List<Subject> allSubjects = getAllSubjectsByUserId(userId);
        String targetDayKorean = convertDayOfWeekToKorean(targetDayEnglish);
        List<Subject> targetDaySubjects = new ArrayList<>();

        for (Subject subject : allSubjects) {
            if (subject.getDays().contains(targetDayKorean)) {
                targetDaySubjects.add(subject);
            }
        }

        if (targetDaySubjects.isEmpty()) {
            logger.warn("No subjects found for user {} on {}", userId, targetDayKorean);
            throw new RuntimeException("No subjects found for user " +userId + " on " + targetDayKorean);
        }

        //특정 요일 과목 정렬
        List<Subject> sortedSubjects = targetDaySubjects.stream()
                .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                .collect(Collectors.toList());

        logger.info("{} subjects found for user {} on {}", targetDaySubjects.size(), userId, targetDayKorean);

        List<TargetDaySubjectResponse> response = sortedSubjects.stream()
                .map(subject -> {
                    String learningState = getLearningStatus(subject.getStartTime(), subject.getEndTime());
                    return new TargetDaySubjectResponse(
                            subject.getId(),
                            subject.getProfileId(),
                            subject.getSubjectName(),
                            subject.getProfessorName(),
                            subject.getDays(),
                            subject.getStartTime(),
                            subject.getEndTime(),
                            subject.getUserId(),
                            learningState
                    );
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

    @Transactional
    public String getLearningStatus(LocalTime startTime, LocalTime endTime) {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isBefore(startTime)) {
            return "학습 전";
        } else if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
            return "녹음 시작";
        } else {
            return "요약 보기";
        }
    }

    @Transactional
    public NextSubjectResponse getNextSubject(Long userId){
        // 모든 요일 과목 조회
        LocalDate day = LocalDate.now();
        LocalDate initialDay = day;//day 복사해두기
        DayOfWeek targetDayEnglish = day.getDayOfWeek();
        List<Subject> allSubjects = getAllSubjectsByUserId(userId);
        if (allSubjects.isEmpty()) {
            logger.warn("No subjects found for userId: {}", userId);
            throw new RuntimeException("No subjects found for userId: " + userId);
        }

        //요일 한국어로 바꾸기
        String targetDayKorean = convertDayOfWeekToKorean(targetDayEnglish);

        //nextSubject 초기 선언
        Subject nextSubject=null;
        do{
            //특정 요일 과목 조회
            List<Subject> targetDaySubjects = new ArrayList<>();
            for (Subject subject : allSubjects) {
                if (subject.getDays().contains(targetDayKorean)) {
                    targetDaySubjects.add(subject);
                }
            }
            if(targetDaySubjects.isEmpty()){
                logger.warn("오늘에 해당하는 과목 못 찾음");
                targetDayKorean = convertNextDay(targetDayKorean);
                continue;
            }

            //현재 시간 이후의 과목 조회
            List<Subject> availableTodaySubjects = new ArrayList<>();
            for (Subject targetDaySubject : targetDaySubjects) {
                LocalTime currentTime = LocalTime.now();
                targetDaySubject.getEndTime().isAfter(currentTime);
                availableTodaySubjects.add(targetDaySubject);
            }
            if (availableTodaySubjects.isEmpty()) {
                logger.warn("오늘에 해당하는 다음과목 못 찾음");
                targetDayKorean = convertNextDay(targetDayKorean);
                continue;
            }

            //과목 정렬
            List<Subject> sortedSubjects = availableTodaySubjects.stream()
                    .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                    .collect(Collectors.toList());

            //다음 과목 찾기
            nextSubject = sortedSubjects.get(0);
            if(nextSubject!=null){
                logger.warn("다음 과목 안 비어있음");
                break;
            }
        }while(day==initialDay); //한 바퀴 돌면 종료

        //못 찾으면 에러발생
        if (nextSubject==null) {
            logger.warn("No NextSubject found for userId: {}", userId);
            throw new RuntimeException("No NextSubject found for userId: " + userId);
        }

        return new NextSubjectResponse(
                nextSubject.getId(),
                nextSubject.getProfileId(),
                nextSubject.getSubjectName(),
                nextSubject.getProfessorName(),
                nextSubject.getDays(),
                nextSubject.getStartTime(),
                nextSubject.getEndTime(),
                nextSubject.getUserId(),
                getLearningStatusByNextSubject(nextSubject.getDays(),nextSubject.getStartTime(), nextSubject.getEndTime())
        );
    }

    public String convertNextDay(String day){
        switch(day){
            case "월": return "화";
            case "화": return "수";
            case "수": return "목";
            case "목": return "금";
            case "금": return "월";
            case "토": return "월";
            case "일": return "월";
        }
        return null;
    }

    public String getLearningStatusByNextSubject(String days, LocalTime startTime, LocalTime endTime) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        DayOfWeek targetDayEnglish = currentDate.getDayOfWeek();
        String targetDayKorean = convertDayOfWeekToKorean(targetDayEnglish);

        if(days.contains(targetDayKorean)&& startTime.isBefore(currentTime) && endTime.isAfter(currentTime)){
            return "녹음시작";
        }
        else return "학습 전";
    }
}
