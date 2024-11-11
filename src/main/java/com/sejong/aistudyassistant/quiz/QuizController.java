package com.sejong.aistudyassistant.quiz;

import com.sejong.aistudyassistant.quiz.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createQuiz(@RequestBody CreateQuizRequest quizRequest) {

        try {
            quizService.createQuizzes(quizRequest.lectureText(), quizRequest.summaryId(), quizRequest.userId());
            return ResponseEntity.status(200).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/learning")
    public ResponseEntity<List<GetLearningQuizResponse>> getLearningQuiz(@RequestBody GetLearningQuizRequest quizRequest) {

        try {
            List<GetLearningQuizResponse> quizResponseList = quizService.getLearningQuiz(quizRequest.userId(), quizRequest.summaryId());
            return ResponseEntity.ok(quizResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/review")
    public ResponseEntity<List<GetReviewQuizResponse>> getReviewQuiz(@RequestBody GetReviewQuizRequest quizRequest) {

        try {
            List<GetReviewQuizResponse> quizResponseList = quizService.getReviewQuiz(quizRequest.userId(), quizRequest.summaryId(), quizRequest.dayInterval());
            return ResponseEntity.ok(quizResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitQuizResponse> submitQuizAnswer(@RequestBody SubmitQuizRequest quizRequest) {

        try {
            return ResponseEntity.ok(quizService.submitQuizAnswer(quizRequest.userId(), quizRequest.quizId(), quizRequest.summaryId(), quizRequest.answer()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/learning/result")
    public ResponseEntity<GetLearningResultResponse> getLearningResult(@RequestBody GetLearningQuizRequest quizRequest) {

        try {
            return ResponseEntity.ok(quizService.getLearningResult(quizRequest.userId(), quizRequest.summaryId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /*@GetMapping("/review/subjects")
    public ResponseEntity<List<GetSubjectStatisticsResponse>> getUserSubjectStatistics(@RequestBody Long userId){

        try {
            return ResponseEntity.ok(quizService.getUserSubjectStatistics(userId));
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }*/

    /*public ResponseEntity<> getUserStatisticsByMonth(){

    }*/
}
