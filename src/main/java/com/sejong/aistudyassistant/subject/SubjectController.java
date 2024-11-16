package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.stt.TranscriptionService;
import com.sejong.aistudyassistant.subject.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/study/Subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    private final JwtUtil jwtUtil;

    SubjectController(SubjectService subjectService, JwtUtil jwtUtil) {
        this.subjectService = subjectService;
        this.jwtUtil = jwtUtil;
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
            @PathVariable Long subjectId, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean status = subjectService.deleteSubject(userId, subjectId);
        return ResponseEntity.ok(new DeleteSubjectResponse(status));
    }

    // 특정 유저의 특정 과목 수정
    @PutMapping("/{subjectId}")
    public ResponseEntity<ModifySubjectResponse> modifySubject(
            @RequestBody ModifySubjectRequest request,
            @PathVariable Long subjectId, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        ModifySubjectResponse response = subjectService.modifySubject(userId, subjectId, request.getModifySubjectName());
        return ResponseEntity.ok(response);
    }
}
