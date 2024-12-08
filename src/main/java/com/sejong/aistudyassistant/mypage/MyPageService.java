package com.sejong.aistudyassistant.mypage;

import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.stt.Transcript;
import com.sejong.aistudyassistant.stt.TranscriptDTO;
import com.sejong.aistudyassistant.stt.TranscriptRepository;
import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MyPageService {

    @Autowired
    private TranscriptRepository transcriptRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public List<TranscriptDTO> getTranscriptsByUserIdAndSubjectId(Long userId, Long subjectId) {
        // 먼저 해당 userId와 subjectId가 일치하는 Subject가 있는지 확인
        Subject subject = subjectRepository.findByUserIdAndId(userId, subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found for this user"));

        List<Transcript> transcripts = transcriptRepository.findBySubject_Id(subjectId);
        return transcripts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TranscriptDTO> getTranscriptsByUserIdAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Transcript> transcripts = transcriptRepository.findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
        return transcripts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private TranscriptDTO convertToDTO(Transcript transcript) {
        return new TranscriptDTO(
                transcript.getId(),
                transcript.getSubject().getId(),
                transcript.getAudioFileName(),
                transcript.getTranscriptText(),
                transcript.getCreatedAt(),
                transcript.getUserId(),
                transcript.getSummaryId(),
                transcript.getQuizId()
        );
    }

    public List<Subject> getAllSubjectsByUserId(Long userId) {
        return subjectRepository.findAllByUserId(userId);
    }
}