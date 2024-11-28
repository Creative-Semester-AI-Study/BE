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
import java.util.*;
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

    public List<GetQuizzesResponse> getQuizzes(Long userId, Long summaryId, Integer dayInterval){

        List<GetQuizzesResponse> quizList=new ArrayList<>();

        if(dayInterval==null)
            dayInterval=0;

        Long startId = calculateStartingQuizId(summaryId,dayInterval).longValue();
        List<Quiz> quizzes=quizRepository.findByUserIdAndSummaryIdAndQuizIdBetween(userId, summaryId, startId,startId+4);

        for(Quiz quiz: quizzes){
            Long quizId=quiz.getQuizId();
            String question=quiz.getQuestion();
            List<QuizOption> optionList=quizOptionRepository.findByQuizQuizId(quizId);
            List<String> options = optionList.stream()
                    .map(QuizOption::getOptionText)
                    .collect(Collectors.toList());
            GetQuizzesResponse responseDTO=new GetQuizzesResponse(quizId,question,options);
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
        //퀴즈 어템프트에서 가장 최근 저장된 애들 5개 가져오기
        //퀴즈 아이디 5개 뽑아낸다음 퀴즈 리스트를 가져온다 (옵션 포함)
        //틀린 여부도 같이 보내줘야 할 것 같은데..+본인이 뭐 선택했는지
        //퀴즈 총 개수(5)랑 맞은 개수도 같이 반환
        //여튼 퀴즈는 저렇게 묶어서 쫙 보내고
        //요약문으로 과목 찾아서 과목 이름, 과목의 날짜 가져온 뒤 날짜로 회차 계산해서 반환
        //씁 걍 퀴즈의 문제, 선지, 정답, 선택한 답, 몇 개 맞았는지 즉 퀴즈 관련된 애들을 dto로 따로 음...클래스로 만드는 게 나을 듯

        List<QuizAttempt> quizAttempt=quizAttemptRepository.findTop5ByOrderByQuizAttemptIdDesc();
        //가장 최근 저장된 퀴즈 시도 객체 5개가 들어와있는 상태





        return (new GetRecentQuizzesResponse(userId));
    }
*/
    private Integer calculateStartingQuizId(Long summaryId, Integer dayInterval) {
        Integer baseQuizId = quizRepository.findFirstQuizIdBySummaryId(summaryId).intValue();
        if (baseQuizId == null) {
            throw new IllegalArgumentException("해당 요약문에 대한 퀴즈가 존재하지 않습니다: " + summaryId);
        }
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