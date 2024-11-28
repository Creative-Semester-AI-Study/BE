package com.sejong.aistudyassistant.quiz;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.quiz.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final JwtUtil jwtUtil;

    public QuizController(QuizService quizService,JwtUtil jwtUtil) {
        this.quizService = quizService;
        this.jwtUtil=jwtUtil;
    }

    @GetMapping("/learning")
    public ResponseEntity<List<GetLearningQuizResponse>> getLearningQuiz(@RequestHeader("Authorization") String authHeader,@RequestBody GetLearningQuizRequest quizRequest) {

        String token = authHeader.replace("Bearer ", "");
        Long userId=jwtUtil.getUserIdFromToken(token);
        try {
            List<GetLearningQuizResponse> quizResponseList = quizService.getLearningQuiz(userId, quizRequest.summaryId());
            return ResponseEntity.ok(quizResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/review")
    public ResponseEntity<List<GetReviewQuizResponse>> getReviewQuiz(@RequestHeader("Authorization") String authHeader,@RequestBody GetReviewQuizRequest quizRequest) {

        String token = authHeader.replace("Bearer ", "");
        Long userId=jwtUtil.getUserIdFromToken(token);
        try {
            List<GetReviewQuizResponse> quizResponseList = quizService.getReviewQuiz(userId, quizRequest.summaryId(), quizRequest.dayInterval());
            return ResponseEntity.ok(quizResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitQuizResponse> submitQuizAnswer(@RequestHeader("Authorization") String authHeader,@RequestBody SubmitQuizRequest quizRequest) {

       String token = authHeader.replace("Bearer ", "");
        Long userId=jwtUtil.getUserIdFromToken(token);
        try {
            return ResponseEntity.ok(quizService.submitQuizAnswer(userId, quizRequest.quizId(), quizRequest.summaryId(), quizRequest.answer()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/learning/result")
    public ResponseEntity<GetLearningResultResponse> getLearningResult(@RequestHeader("Authorization") String authHeader,@RequestBody GetLearningResultRequest quizRequest) {

        String token = authHeader.replace("Bearer ", "");
        Long userId=jwtUtil.getUserIdFromToken(token);
        try {
            return ResponseEntity.ok(quizService.getLearningResult(userId, quizRequest.summaryId(),quizRequest.subjectId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
