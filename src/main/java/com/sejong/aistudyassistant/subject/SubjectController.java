package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.DeleteSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.ModifySubjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PutExchange;

@RestController
@RequestMapping("/study/myPage")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    // 과목 생성 엔드포인트
    @PostMapping("/createSubject")
    public ResponseEntity<CreateSubjectResponse> createSubject(@RequestBody CreateSubjectRequest request) {
        CreateSubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.ok(response);
    }

    // 특정 유저의 특정 과목 삭제
    @DeleteMapping("/{userId}/{subjectId}")
    public ResponseEntity<DeleteSubjectResponse> deleteSubject(
            @PathVariable Long userId,
            @PathVariable Long subjectId) {
        boolean status = subjectService.deleteSubject(userId, subjectId);
        return ResponseEntity.ok(new DeleteSubjectResponse(status));
    }

    // 특정 유저의 특정 과목 수정
    @PutMapping("/{userId}")
    public ResponseEntity<ModifySubjectRequest> modifySubject(
            @PathVariable Long userId,
            @PathVariable Long subjectId,
            @PathVariable Long profileId,
            @PathVariable Long textTransformId,
            @PathVariable Long summaryId,
            @PathVariable Long quizId,
            @PathVariable String subjectName) {
            ModifySubjectRequest response = subjectService.modifySubject(userId, subjectId, profileId, textTransformId, summaryId, quizId, subjectName);
            return ResponseEntity.ok(response);
    }

}
