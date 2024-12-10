package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.subject.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/study/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    private final JwtUtil jwtUtil;

    SubjectController(SubjectService subjectService, JwtUtil jwtUtil) {
        this.subjectService = subjectService;
        this.jwtUtil = jwtUtil;
    }

    //특정 과목 조회
    @GetMapping("/{subjectId}")
    public ResponseEntity<CheckSubjectResponse> checkSubject(@PathVariable("subjectId") Long subjectId, @RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        CheckSubjectResponse response = subjectService.checkSubject(userId, subjectId);
        return ResponseEntity.ok(response);
    }


    // 과목 생성 엔드포인트
    @PostMapping("/createSubject")
    public ResponseEntity<CreateSubjectResponse> createSubject(@RequestBody CreateSubjectRequest request,
                                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        CreateSubjectResponse response = subjectService.createSubject(request, userId);
        return ResponseEntity.ok(response);
    }

    // 특정 유저의 특정 과목 삭제
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<DeleteSubjectResponse> deleteSubject(
            @PathVariable("subjectId") Long subjectId, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean status = subjectService.deleteSubject(userId, subjectId);
        return ResponseEntity.ok(new DeleteSubjectResponse(status));
    }

    // 특정 유저의 특정 과목 수정
    @PutMapping("/{subjectId}")
    public ResponseEntity<ModifySubjectResponse> modifySubject(
            @RequestBody ModifySubjectRequest request,
            @PathVariable("subjectId") Long subjectId, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        ModifySubjectResponse response = subjectService.modifySubject(userId, subjectId, request.getModifySubjectName());
        return ResponseEntity.ok(response);
    }

    // 특정 날짜 과목 조회
    @GetMapping("/check/{date}")
    public ResponseEntity<List<TargetDaySubjectResponse>> getSubjectsByUserIdAndDate(@PathVariable("date") LocalDate date,
                                                                                     @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<TargetDaySubjectResponse> response = subjectService.getSubjectsByUserIdAndDate(userId,date);
        return ResponseEntity.ok(response);
    }

    // 다음 과목 조회
    @GetMapping("/nextSubject")
    public ResponseEntity<NextSubjectResponse> getNextSubject(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        NextSubjectResponse response = subjectService.getNextSubject(userId);
        return ResponseEntity.ok(response);
    }
}
