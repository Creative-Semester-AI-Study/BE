package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import com.sejong.aistudyassistant.subject.dto.DeleteSubjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
