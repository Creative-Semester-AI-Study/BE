package com.sejong.aistudyassistant.subject;

import com.sejong.aistudyassistant.subject.dto.CreateSubjectRequest;
import com.sejong.aistudyassistant.subject.dto.CreateSubjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public CreateSubjectResponse createSubject(CreateSubjectRequest request) {
        Subject newSubject = new Subject();
        newSubject.setProfileId(request.getProfileId());
        newSubject.setTextTransformId(request.getTextTransformId());
        newSubject.setSummaryId(request.getSummaryId());
        newSubject.setQuizId(request.getQuizId());
        newSubject.setSubjectName(request.getSubjectName());

        Subject savedSubject = subjectRepository.save(newSubject);

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
