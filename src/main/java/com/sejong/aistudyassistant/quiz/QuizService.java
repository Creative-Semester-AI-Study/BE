package com.sejong.aistudyassistant.quiz;

import com.sejong.aistudyassistant.quiz.Entity.Quiz;
import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import com.sejong.aistudyassistant.quiz.Entity.QuizOption;
import com.sejong.aistudyassistant.quiz.Entity.QuizStatistics;
import com.sejong.aistudyassistant.quiz.Repository.QuizAttemptRepository;
import com.sejong.aistudyassistant.quiz.Repository.QuizOptionRepository;
import com.sejong.aistudyassistant.quiz.Repository.QuizRepository;
import com.sejong.aistudyassistant.quiz.Repository.QuizStatisticsRepository;
import com.sejong.aistudyassistant.quiz.dto.*;
import com.sejong.aistudyassistant.subject.Subject;
import com.sejong.aistudyassistant.subject.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final SubjectRepository subjectRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizStatisticsRepository quizStatisticsRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public QuizService(QuizRepository quizRepository,QuizOptionRepository quizOptionRepository,SubjectRepository subjectRepository,QuizAttemptRepository quizAttemptRepository,QuizStatisticsRepository quizStatisticsRepository) {
        this.quizRepository = quizRepository;
        this.quizOptionRepository=quizOptionRepository;
        this.subjectRepository=subjectRepository;
        this.quizAttemptRepository=quizAttemptRepository;
        this.quizStatisticsRepository=quizStatisticsRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void createQuizzes(String lectureText, Long summaryId, Long userId) throws Exception {
        // ChatGPT API 요청 설정
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ChatGPT 요청 메시지에 20개의 퀴즈 생성을 명시
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant who generates quiz questions based on lecture text."),
                        Map.of("role", "user", "content", "Generate 20 unique quizzes in JSON format with 'question', 'options', and 'correctAnswer' based on the following lecture text:\n" + lectureText)
                )
        );

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

        // ChatGPT API 호출
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Failed to generate quizzes: " + response.getStatusCode());
            throw new RuntimeException("Failed to generate quizzes");
        }

        // 응답에서 JSON 파싱
        String responseContent = response.getBody();
        logger.info("Response content: " + responseContent);

        JsonNode rootNode = objectMapper.readTree(responseContent);
        JsonNode firstChoice = rootNode.path("choices").get(0);
        if (firstChoice.isMissingNode()) {
            logger.error("No choices found in response");
            throw new RuntimeException("No choices found in response");
        }

        String content = firstChoice.path("message").path("content").asText();
        logger.info("Quiz content: " + content);

        // JSON 배열 형태의 20개의 퀴즈 데이터 파싱
        JsonNode quizArray = objectMapper.readTree(content);

        int count = 0;
        for (JsonNode quizJson : quizArray) {
            if (count >= 20) break; // 최대 20개의 퀴즈만 저장

            // JSON 데이터에서 각 필드를 추출
            String question = quizJson.get("question").asText();
            List<String> optionsList = objectMapper.convertValue(
                    quizJson.get("options"), new TypeReference<List<String>>() {}
            );
            String correctAnswer = quizJson.get("correctAnswer").asText();

            // Quiz 객체 생성 및 데이터베이스 저장
            Quiz quiz = new Quiz();
            quiz.setSummaryId(summaryId);
            quiz.setQuestion(question);
            quiz.setCorrectAnswer(correctAnswer);
            quiz.setUserId(userId);

            List<QuizOption> options = new ArrayList<>();
            for (String optionText : optionsList) {
                QuizOption option = new QuizOption();
                option.setQuiz(quiz);  // Quiz와 연결
                option.setOptionText(optionText);
                options.add(option);
            }
            quiz.setOptions(options);

            try {
                quizRepository.save(quiz);
                count++;
            } catch (Exception e) {
                logger.error("Failed to save quiz: ", e);
                throw e;
            }
        }
    }

    List<GetLearningQuizResponse> getLearningQuiz(Long userId, Long summaryId){

        List<GetLearningQuizResponse> quizList=new ArrayList<>();
        List<Quiz> quizzes=quizRepository.findTop5ByUserIdAndSummaryIdOrderByQuizId(userId,summaryId);

        for(Quiz quiz: quizzes){
            Long quizId=quiz.getQuizId();
            String question=quiz.getQuestion();
            List<QuizOption> optionList=quizOptionRepository.findByQuizQuizId(quizId);
            List<String> options = optionList.stream()
                    .map(QuizOption::getOptionText)
                    .collect(Collectors.toList());
            GetLearningQuizResponse responseDTO=new GetLearningQuizResponse(quizId,question,options);
            quizList.add(responseDTO);
        }
        return quizList;
    }

    public List<GetReviewQuizResponse> getReviewQuiz(Long userId, Long summaryId, Integer dayInterval){

        List<GetReviewQuizResponse> quizList=new ArrayList<>();
        Long startId = calculateStartingQuizId(dayInterval).longValue();
        List<Quiz> quizzes=quizRepository.findByUserIdAndSummaryIdAndQuizIdBetween(userId, summaryId, startId,startId+4);

        for(Quiz quiz: quizzes){
            Long quizId=quiz.getQuizId();
            String question=quiz.getQuestion();
            List<QuizOption> optionList=quizOptionRepository.findByQuizQuizId(quizId);
            List<String> options = optionList.stream()
                    .map(QuizOption::getOptionText)
                    .collect(Collectors.toList());
            GetReviewQuizResponse responseDTO=new GetReviewQuizResponse(quizId,question,options);
            quizList.add(responseDTO);
        }
        return quizList;
    }

    public SubmitQuizResponse submitQuizAnswer(Long userId, Long quizId, Long summaryId, String answer){

        Optional<Quiz> quiz=quizRepository.findById(quizId); //본인이 푸는 퀴즈 (답 포함되어 있음)

        QuizAttempt quizAttempt=new QuizAttempt(); //시도 객체 만들고 기본 값 저장
        quizAttempt.setQuizId(quizId);
        quizAttempt.setUserId(userId);
        quizAttempt.setSummaryId(summaryId);
        quizAttempt.setAttemptDate(LocalDate.now().toString());

        QuizStatistics quizStatistics = quizStatisticsRepository.findByUserIdAndSummaryId(userId, summaryId)
                .orElseGet(() -> { // 없으면 새로 생성
                    QuizStatistics newStats = new QuizStatistics();
                    newStats.setUserId(userId);
                    newStats.setSummaryId(summaryId);
                    newStats.setSubjectId(subjectRepository.findBySummaryId(summaryId).getSubjectId());
                    newStats.setTotalQuestions(20); // 기본 총 문제 수 설정
                    newStats.setCorrectAnswers(0); // 맞은 문제 수 초기화
                    return newStats;
                });

        if(answer.equals(quiz.get().getCorrectAnswer())){ //선택한 답과 비교해서 true/false 저장
            quizAttempt.setCorrect(true);
            quizStatistics.setCorrectAnswers(quizStatistics.getCorrectAnswers()+1);//이거는 나중에 전체 통계 낼 때 해당함
        }
        else
            quizAttempt.setCorrect(false);

        quizAttemptRepository.save(quizAttempt);
        quizStatisticsRepository.save(quizStatistics);

        SubmitQuizResponse quizResponse=new SubmitQuizResponse(quiz.get().getCorrectAnswer(),answer,quizAttempt.getCorrect());
        return quizResponse;
    }

    public GetLearningResultResponse getLearningResult(Long userId, Long summaryId){

        List<QuizAttempt> quizList=quizAttemptRepository.findByUserIdAndSummaryIdAndQuizIdBetween(userId,summaryId,1L,5L);
        int correctAnswers=0;

        for(QuizAttempt quiz: quizList){
            correctAnswers=correctAnswers+(quiz.getCorrect()? 1:0);
        }

        Subject subject=subjectRepository.findBySummaryId(summaryId);
        QuizAttempt quizAttempt=quizAttemptRepository.findByQuizId(1L);

        GetLearningResultResponse resultResponse=new GetLearningResultResponse(userId, summaryId,quizAttempt.getAttemptDate(),subject.getSubjectName(),5,correctAnswers);
        return resultResponse;
    }

   /* public List<GetSubjectStatisticsResponse> getUserSubjectStatistics(Long userId){


    }*/

    private Integer calculateStartingQuizId(Integer dayInterval){

        switch (dayInterval){
            case 1:
                return 6;
            case 7:
                return 11;
            case 30:
                return 16;
            default:
                throw new IllegalArgumentException("잘못된 주기입니다: " + dayInterval);
        }
    }
}