package com.sejong.aistudyassistant.quiz;

import com.sejong.aistudyassistant.jwt.JwtUtil;
import com.sejong.aistudyassistant.profile.ProfileRepository;
import com.sejong.aistudyassistant.quiz.Entity.Quiz;
import com.sejong.aistudyassistant.quiz.Entity.QuizAttempt;
import com.sejong.aistudyassistant.quiz.Entity.QuizOption;
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
    private final JwtUtil jwtUtil;
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final SubjectRepository subjectRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizStatisticsRepository quizStatisticsRepository;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public QuizService(QuizRepository quizRepository,QuizOptionRepository quizOptionRepository,SubjectRepository subjectRepository,QuizAttemptRepository quizAttemptRepository,QuizStatisticsRepository quizStatisticsRepository,ProfileRepository profileRepository,JwtUtil jwtUtil) {
        this.quizRepository = quizRepository;
        this.quizOptionRepository=quizOptionRepository;
        this.subjectRepository=subjectRepository;
        this.quizAttemptRepository=quizAttemptRepository;
        this.quizStatisticsRepository=quizStatisticsRepository;
        this.profileRepository=profileRepository;
        this.jwtUtil=jwtUtil;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void createQuizzes(String lectureText, Long summaryId, Long userId) throws Exception {

        String apiUrl = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant who generates quiz questions based on lecture text in Korean."),
                        Map.of("role", "user", "content", "Generate exactly 30 unique quizzes in JSON format with 'question', 'options', and 'correctAnswer' based on the following lecture text in Korean:\n" + lectureText)
                )
        );

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Failed to generate quizzes: " + response.getStatusCode());
            throw new RuntimeException("Failed to generate quizzes");
        }

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

        JsonNode quizArray = objectMapper.readTree(content);

        int count = 0;
        for (JsonNode quizJson : quizArray) {
            if (count >= 30) break;

            String question = quizJson.get("question").asText();
            List<String> optionsList = objectMapper.convertValue(
                    quizJson.get("options"), new TypeReference<List<String>>() {}
            );
            String correctAnswer = quizJson.get("correctAnswer").asText();

            Quiz quiz = new Quiz();
            quiz.setSummaryId(summaryId);
            quiz.setQuestion(question);
            quiz.setCorrectAnswer(correctAnswer);
            quiz.setUserId(userId);

            List<QuizOption> options = new ArrayList<>();
            for (String optionText : optionsList) {
                QuizOption option = new QuizOption();
                option.setQuiz(quiz);
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
        System.out.println("1");
        Long startId = calculateStartingQuizId(summaryId,dayInterval).longValue();
        System.out.println(startId);
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

        Optional<Quiz> quiz=quizRepository.findById(quizId);

        QuizAttempt quizAttempt=new QuizAttempt();
        quizAttempt.setQuizId(quizId);
        quizAttempt.setUserId(userId);
        quizAttempt.setSummaryId(summaryId);
        quizAttempt.setAttemptDate(LocalDate.now().toString());

        if(answer.equals(quiz.get().getCorrectAnswer())){
            quizAttempt.setCorrect(true);
        }
        else
            quizAttempt.setCorrect(false);

        QuizAttempt firstQuizAttempt=quizAttemptRepository.findByQuizId(quizId);

        if(firstQuizAttempt==null){
            quizAttemptRepository.save(quizAttempt);
        }else {
            quizAttemptRepository.delete(firstQuizAttempt);
            quizAttemptRepository.save(quizAttempt);
        }

        SubmitQuizResponse quizResponse=new SubmitQuizResponse(quiz.get().getCorrectAnswer(),answer,quizAttempt.getCorrect());
        return quizResponse;
    }

    public GetQuizResultResponse getQuizResult(Long userId, Long summaryId, Long subjectId, Integer dayInterval){

        Long startId=calculateStartingQuizId(summaryId,dayInterval).longValue();

        List<QuizAttempt> quizList=quizAttemptRepository.findByUserIdAndSummaryIdAndQuizIdBetween(userId,summaryId,startId,startId+4);
        int correctAnswers=0;

        for(QuizAttempt quiz: quizList){
            correctAnswers=correctAnswers+(quiz.getCorrect()? 1:0);
        }

        Optional<Subject> subject=subjectRepository.findById(subjectId);
        QuizAttempt quizAttempt=quizAttemptRepository.findByQuizId(1L);

        GetQuizResultResponse resultResponse=new GetQuizResultResponse(userId, summaryId,quizAttempt.getAttemptDate(),subject.get().getSubjectName(),5,correctAnswers);
        return resultResponse;
    }

    /*
    //최근 퀴즈 5개 가져오는 거
    public GetRecentQuizzesResponse getRecentQuizzes(Long userId){

    }
*/
    private Integer calculateStartingQuizId(Long summaryId, Integer dayInterval) {
        System.out.println("2");
        Integer baseQuizId = quizRepository.findFirstQuizIdBySummaryId(summaryId).intValue();
        System.out.println("3");
        if (baseQuizId == null) {
            throw new IllegalArgumentException("해당 요약문에 대한 퀴즈가 존재하지 않습니다: " + summaryId);
        }
        System.out.println("4");
        switch (dayInterval) {
            case 0:
                return baseQuizId;
            case 1:
                return baseQuizId + 5;
            case 3:
                return baseQuizId + 10;
            case 7:
                return baseQuizId + 15;
            case 15:
                return baseQuizId + 20;
            case 30:
                return baseQuizId + 25;
            default:
                throw new IllegalArgumentException("잘못된 주기입니다: " + dayInterval);
        }
    }
}