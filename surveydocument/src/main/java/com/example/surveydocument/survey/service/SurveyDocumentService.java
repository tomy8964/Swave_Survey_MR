package com.example.surveydocument.survey.service;

import com.example.surveydocument.restAPI.service.OuterRestApiSurveyDocumentService;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;

import com.example.surveydocument.survey.domain.*;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.repository.choice.ChoiceRepository;
import com.example.surveydocument.survey.repository.date.DateRepository;
import com.example.surveydocument.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.surveydocument.survey.repository.survey.SurveyRepository;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.repository.template.choiceTemplate.ChoiceTemplateRepository;
import com.example.surveydocument.survey.repository.template.questionTemplate.QuestionTemplateRepository;
import com.example.surveydocument.survey.repository.template.surveyTemplate.SurveyTemplateRepository;
import com.example.surveydocument.survey.repository.wordCloud.WordCloudRepository;
import com.example.surveydocument.survey.request.*;
import com.example.surveydocument.survey.response.ChoiceDetailDto;
import com.example.surveydocument.survey.response.QuestionDetailDto;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.response.WordCloudDto;
import com.example.surveydocument.user.domain.User;
import com.example.surveydocument.util.page.PageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.surveydocument.survey.domain.DateManagement.*;
import static com.example.surveydocument.survey.domain.DesignTemplate.designRequestToEntity;

//import static com.example.surveyAnswer.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableTransactionManagement
public class SurveyDocumentService {
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;

    private final QuestionDocumentRepository questionDocumentRepository;
    private final ChoiceRepository choiceRepository;
    private final WordCloudRepository wordCloudRepository;
    private final DateRepository dateRepository;
    private final OuterRestApiSurveyDocumentService apiService;


    private final SurveyTemplateRepository surveyTemplateRepository;
    private final QuestionTemplateRepository questionTemplateRepository;
    private final ChoiceTemplateRepository choiceTemplateRepository;
    @Value("${gateway.host}")
    private String gateway;
    Random random = new Random();
    private List<ReliabilityQuestion> questions;
    private int reliabilityquestionNumber;
    @PersistenceContext
    private final EntityManager em;
    // redis 분산 락 사용
    // 분산 락은 Transactional 과 같이 진행되지 않아서 따로 관리로직을 만들어야한다
    private final RedissonClient redissonClient;
    private final PlatformTransactionManager transactionManager;
    public ReliabilityQuestion reliabilityQuestion() throws JsonProcessingException {
        String jsonString = "{\"questionRequest\":[" +
                "{\"title\":\"이 문항에는 어느것도 아니다를 선택해주세요.\",\"type\":2,\"correct_answer\":\"어느것도 아니다.\",\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 매우 부정적이다를 선택해주세요.\",\"correct_answer\":\"매우 부정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 약간 부정적이다를 선택해주세요.\",\"correct_answer\":\"약간 부정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 약간 긍정적이다를 선택해주세요.\",\"correct_answer\":\"약간 긍정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 매우 긍정적이다를 선택해주세요.\",\"correct_answer\":\"매우 긍정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 질문에 대한 답변을 생각해보지 않고 무작위로 선택했습니다.\",\"correct_answer\":\"그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"그렇다.\"},{\"id\":2,\"choiceName\":\"그렇지 않다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"그렇다고 말할 수 있다.\"}]}," +
                "{\"title\":\"이 설문에 진심으로 참여하고 있나요?\",\"correct_answer\":\"매우 그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"전혀 아니다.\"},{\"id\":2,\"choiceName\":\"아니다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"어느 정도 아니다.\"},{\"id\":5,\"choiceName\":\"매우 그렇다.\"}]}," +
                "{\"title\":\"메뚜기의 종류를 3000개이상 알고 있다\",\"correct_answer\":\"아니다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"아니다.\"},{\"id\":2,\"choiceName\":\"그렇다.\"}]}," +
                "{\"title\":\"설문조사의 목적과 내용을 이해하고 진정성을 유지하며 응답하고 있습니까?\",\"correct_answer\":\"매우 그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"전혀 아니다.\"},{\"id\":2,\"choiceName\":\"아니다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"어느 정도 아니다.\"},{\"id\":5,\"choiceName\":\"매우 그렇다.\"}]}," +
                "{\"title\":\"이 설문조사에 참여하는 데 얼마나 진지하게 접근하고 있나요?\",\"correct_answer\":\"매우 진지하게 접근하고 있다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 진지하게 접근하고 있다.\"},{\"id\":2,\"choiceName\":\"부주의하게 접근하고 있다.\"},{\"id\":3,\"choiceName\":\"아주 부주의하게 접근하고 있다..\"}]}" +
                "]}";

        ObjectMapper objectMapper = new ObjectMapper();
        ReliabilityQuestionRequest questionRequest = objectMapper.readValue(jsonString, ReliabilityQuestionRequest.class);

        reliabilityquestionNumber=random.nextInt(10);

        // Access the converted Java object
        questions = questionRequest.getQuestionRequest();
        ReliabilityQuestion question1=questions.get(reliabilityquestionNumber);
        List<ReliabilityChoice> Rchoices = question1.getChoiceList();
        return question1;

//        for (ReliabilityQuestion question : questions) {
//            System.out.println("Title: " + question.getTitle());
//            System.out.println("Type: " + question.getType());
//            System.out.println("Correct Answer: " + question.getCorrect_answer());
//            List<ReliabilityChoice> Rchoices = question.getChoiceList();
//            for (ReliabilityChoice choice : Rchoices) {
//                System.out.println("Choice ID: " + choice.getId());
//                System.out.println("Choice Name: " + choice.getChoiceName());
//            }
//        }
    }
    @Transactional
    // todo : 날짜 생성
    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException, UnknownHostException {

        // 유저 정보 받아오기
        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser(request);
        Survey userSurvey = getUser.getSurvey();

        // 유저에 Survey 가 없다면 넣어주기
        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(getUser)
                    .surveyDocumentList(new ArrayList<>())
                    .build();
            surveyRepository.save(userSurvey);
        }

        Survey survey = surveyRepository.findByUser(getUser.getId());
        createTest(userSurvey, surveyRequest);

        // User Module 에 저장된 Survey 보내기
        apiService.sendSurveyToUser(request,userSurvey);
    }

    public void createTest(Survey userSurvey, SurveyRequestDto surveyRequest) {
        // Survey Request 를 Survey Document 에 저장하기
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .survey(userSurvey)
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .questionDocumentList(new ArrayList<>())
                .reliability(surveyRequest.getReliability()) // 진정성 검사
                .countAnswer(0)
                .build();
        surveyDocumentRepository.save(surveyDocument);

        // 디자인 저장
        // Design Request To Entity
        DesignTemplate design = designRequestToEntity(
                surveyRequest.getFont(),
                surveyRequest.getFontSize(),
                surveyRequest.getBackColor()
        );

        // 날짜 저장
        // Date Request To Entity
        DateManagement dateManagement = dateRequestToEntity(
                surveyRequest.getStartDate(),
                surveyRequest.getEndDate(),
                surveyDocumentRepository.findById(surveyDocument.getId()).get()
        );

        dateRepository.save(dateManagement);

        surveyDocument.setDesign(design);
        surveyDocument.setDate(dateManagement);
        surveyDocumentRepository.findById(surveyDocument.getId());

        // 설문 문항
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            // 설문 문항 저장
            QuestionDocument questionDocument = QuestionDocument.builder()
                    .surveyDocument(surveyDocumentRepository.findById(surveyDocument.getId()).get())
                    .title(questionRequestDto.getTitle())
                    .questionType(questionRequestDto.getType())
                    .build();
            questionDocumentRepository.save(questionDocument);

            if(questionRequestDto.getType() == 0) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            questionDocument.setChoiceList(new ArrayList<>());
            for(ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
                Choice choice = Choice.builder()
                        .question_id(questionDocumentRepository.findById(questionDocument.getId()).get())
                        .title(choiceRequestDto.getChoiceName())
                        .count(0)
                        .build();
                choiceRepository.save(choice);
                questionDocument.setChoice(choice);
            }
            surveyDocument.setQuestion(questionDocument);
            // choice 가 추가될 때마다 변경되는 Question Document 정보 저장
            questionDocumentRepository.flush();
        }
        // question 이 추가될 때마다 변경되는 Survey Document 정보 저장
        surveyDocumentRepository.flush();

        // Survey 에 SurveyDocument 저장
        userSurvey.setDocument(surveyDocument);
        surveyRepository.flush();
    }

    public void createTemplateSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException, UnknownHostException {

//        // 유저 정보 받아오기
//        // User Module 에서 현재 유저 가져오기
//        User getUser = apiService.getCurrentUserFromUser(request);
//        Survey userSurvey = getUser.getSurvey();
//
//        if(userSurvey == null) {
//            userSurvey = Survey.builder()
//                    .user(getUser)
//                    .surveyDocumentList(new ArrayList<>())
//                    .build();
//            surveyRepository.save(userSurvey);
//        }

        // Survey Request 를 Survey Document 에 저장하기
        SurveyTemplate surveyTemplate = SurveyTemplate.builder()
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .questionTemplateList(new ArrayList<>())
                .surveyAnswerList(new ArrayList<>())
                .reliability(surveyRequest.getReliability())
                .countAnswer(0)
                .build();
        surveyTemplateRepository.save(surveyTemplate);

        DesignTemplate design = designRequestToEntity(
                surveyRequest.getFont(),
                surveyRequest.getFontSize(),
                surveyRequest.getBackColor()
        );
        surveyTemplate.setDesignTemplate(design);
        // 설문 문항
        surveyTemplateRepository.findById(surveyTemplate.getId());
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            // 설문 문항 저장
            QuestionTemplate questionTemplate = QuestionTemplate.builder()
                    .surveyTemplate(surveyTemplateRepository.findById(surveyTemplate.getId()).get())
                    .title(questionRequestDto.getTitle())
                    .questionType(questionRequestDto.getType())
                    .build();
            questionTemplateRepository.save(questionTemplate);

            if(questionRequestDto.getType() == 0) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            questionTemplate.setChoiceTemplateList(new ArrayList<>());
            for(ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
                ChoiceTemplate choiceTemplate = ChoiceTemplate.builder()
                        .question_template_id(questionTemplateRepository.findById(questionTemplate.getId()).get())
                        .title(choiceRequestDto.getChoiceName())
                        .count(0)
                        .build();
                choiceTemplateRepository.save(choiceTemplate);
                questionTemplate.setChoice(choiceTemplate);
            }
            surveyTemplate.setQuestion(questionTemplate);
            // choice 가 추가될 때마다 변경되는 Question Document 정보 저장
            questionTemplateRepository.flush();
        }
        // question 이 추가될 때마다 변경되는 Survey Document 정보 저장
        surveyTemplateRepository.flush();

//        // Survey 에 SurveyDocument 저장
//        userSurvey.setDocument(surveyDocument);
//        surveyRepository.flush();
//
//        // User Module 에 저장된 Survey 보내기
//        apiService.sendSurveyToUser(request,userSurvey);
    }

    // gird method 로 SurveyDocument 조회
    public List<SurveyDocument> readSurveyListByGrid(HttpServletRequest request, PageRequestDto pageRequest) {

        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser(request);

        return surveyRepository.getSurveyDocumentListGrid(getUser, pageRequest);
    }

    // list method 로 SurveyDocument 조회
    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequestDto pageRequest) throws Exception {


        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser(request);

        PageRequest page = PageRequest.builder()
                .page(pageRequest.getPage())
                .method(pageRequest.getMethod())
                .sortProperties(pageRequest.getSort1()) // date or title
                .direct(pageRequest.getSort2()) // ascending or descending
                .build();

        // Request Method
        // 1. view Method : grid or list
        // 2. what page number
        // 3. sort on What : date or title
        // 4. sort on How : ascending or descending
        Pageable pageable = page.of(page.getSortProperties(), page.getDirection(page.getDirect()));

        return surveyRepository.surveyDocumentPaging(getUser, pageable);
    }

    public SurveyDetailDto readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {

        return getSurveyDetailDto(id);
    }

    public SurveyDetailDto readSurveyTemplate(Long id){
        return getSurveyTemplateDetailDto(id);
    }

    public SurveyDocument getSurveyDocument(Long surveyDocumentId) {
        return surveyDocumentRepository.findById(surveyDocumentId).get();
    }

    //count +1
    @Transactional
    public void countChoice(Long choiceId) {
        Optional<Choice> findChoice = choiceRepository.findById(choiceId);

        if (findChoice.isPresent()) {
            choiceRepository.incrementCount(choiceId);
        }
    }

    // 설문 응답자 수 + 1
    // 분산락 실행
    public void countSurveyDocument(Long surveyDocumentId) throws Exception {
        // survey document id 값을 키로 하는 lock 을 조회합니다.
        RLock rLock = redissonClient.getLock("survey : lock");
        // Lock 획득 시도
        boolean isLocked = rLock.tryLock(5, 10, TimeUnit.SECONDS);
        log.info(Thread.currentThread().getName() + " lock 획득 시도!");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info(Thread.currentThread().getName() + " Transaction 시작");

        // @Transactional 대신 코드로 트랜잭션을 관리한다
        try {
            if(!isLocked) {
                throw new MessagingException("failed to get RLock");
            }

            // 조회수 증가 로직 실행
            try {
                SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).orElse(null);
                surveyRepository.surveyDocumentCount(surveyDocument);

                // 실행하면 커밋후 트랜잭션 종료
                transactionManager.commit(status);
                log.info(Thread.currentThread().getName() + " 커밋 후 트랜잭션 종료");
            } catch (RuntimeException e) {
                // 로직 실행 중 예외가 발생하면 롤백
                transactionManager.rollback(status);
                log.info(Thread.currentThread().getName() + " 로직 실행 실패");
                throw new Exception(e.getMessage());
            }

        } catch (InterruptedException e) {
            throw new Exception("Thread Interrupted");
        } finally {
            // 로직 수행이 끝나면 Lock 반환
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info(Thread.currentThread().getName() + " Lock 해제");
            }
        }
    }

    // SurveyDocument Response 보낼 SurveyDetailDto로 변환하는 메서드
    private SurveyDetailDto getSurveyDetailDto(Long surveyDocumentId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
        SurveyDetailDto surveyDetailDto = new SurveyDetailDto();
        ReliabilityQuestion reliabilityQuestion = null;
        QuestionDetailDto reliabilityQuestionDto = new QuestionDetailDto();
        List<ChoiceDetailDto> reliabiltyChoiceDtos = new ArrayList<>();
        if(surveyDocument.getReliability()){
            try {
                Long l = Long.valueOf(-1);
                reliabilityQuestion = reliabilityQuestion();
                reliabilityQuestionDto.setId(l);
                reliabilityQuestionDto.setTitle(reliabilityQuestion.getTitle());
                reliabilityQuestionDto.setQuestionType(reliabilityQuestion.getType());
                List<ReliabilityChoice> Rchoices = reliabilityQuestion.getChoiceList();
                for (ReliabilityChoice choice : Rchoices) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setId(l);
                    choiceDto.setTitle(choice.getChoiceName());
                    choiceDto.setCount(0);
                    reliabiltyChoiceDtos.add(choiceDto);
                }
                reliabilityQuestionDto.setChoiceList(reliabiltyChoiceDtos);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyDetailDto.setId(surveyDocument.getId());
        surveyDetailDto.setTitle(surveyDocument.getTitle());
        surveyDetailDto.setDescription(surveyDocument.getDescription());
        surveyDetailDto.setReliability(surveyDocument.getReliability());


        surveyDetailDto.setFont(surveyDocument.getDesign().getFont());
        surveyDetailDto.setFontSize(surveyDocument.getDesign().getFontSize());
        surveyDetailDto.setBackColor(surveyDocument.getDesign().getBackColor());


        List<QuestionDetailDto> questionDtos = new ArrayList<>();
        for (QuestionDocument questionDocument : surveyDocument.getQuestionDocumentList()) {
            QuestionDetailDto questionDto = new QuestionDetailDto();
            questionDto.setId(questionDocument.getId());
            questionDto.setTitle(questionDocument.getTitle());
            questionDto.setQuestionType(questionDocument.getQuestionType());

            // question type에 따라 choice 에 들어갈 내용 구분
            // 주관식이면 choice title에 주관식 응답을 저장??
            // 객관식 찬부식 -> 기존 방식 과 똑같이 count를 올려서 저장
            List<ChoiceDetailDto> choiceDtos = new ArrayList<>();
            if (questionDocument.getQuestionType() == 0) {
                // 주관식 답변들 리스트
                // REST API GET questionAnswersByCheckAnswerId
                List<QuestionAnswer> questionAnswersByCheckAnswerId = apiService.getQuestionAnswersByCheckAnswerId(questionDocument.getId());
                for (QuestionAnswer questionAnswer : questionAnswersByCheckAnswerId) {
                    // 그 중에 주관식 답변만
                    if (questionAnswer.getQuestionType() == 0) {
                        ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                        choiceDto.setId(questionAnswer.getId());
                        choiceDto.setTitle(questionAnswer.getCheckAnswer());
                        choiceDto.setCount(0);

                        choiceDtos.add(choiceDto);
                    }
                }
            } else {
                for (Choice choice : questionDocument.getChoiceList()) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setId(choice.getId());
                    choiceDto.setTitle(choice.getTitle());
                    choiceDto.setCount(choice.getCount());

                    choiceDtos.add(choiceDto);
                }
            }
            questionDto.setChoiceList(choiceDtos);

            List<WordCloudDto> wordCloudDtos = new ArrayList<>();
            for (WordCloud wordCloud : questionDocument.getWordCloudList()) {
                WordCloudDto wordCloudDto = new WordCloudDto();
                wordCloudDto.setId(wordCloud.getId());
                wordCloudDto.setTitle(wordCloud.getTitle());
                wordCloudDto.setCount(wordCloud.getCount());

                wordCloudDtos.add(wordCloudDto);
            }
            questionDto.setWordCloudDtos(wordCloudDtos);

            questionDtos.add(questionDto);
        }
        if(surveyDocument.getReliability()) {
            reliabilityquestionNumber = random.nextInt(questionDtos.size());
            questionDtos.add(reliabilityquestionNumber, reliabilityQuestionDto);
        }
        surveyDetailDto.setQuestionList(questionDtos);

        log.info(String.valueOf(surveyDetailDto));
        return surveyDetailDto;
    }

    public Choice getChoice(Long id) {
        Optional<Choice> byId = choiceRepository.findById(id);
        return byId.get();
    }

    public QuestionDocument getQuestion(Long id) {
        Optional<QuestionDocument> byId = questionDocumentRepository.findById(id);
        return byId.get();
    }

    public QuestionDocument getQuestionByChoiceId(Long id) {
        return choiceRepository.findById(id).get().getQuestion_id();
    }

    @Transactional
    public void setWordCloud(Long id, List<WordCloudDto> wordCloudDtos) {
        List<WordCloud> wordCloudList = new ArrayList<>();
        for (WordCloudDto wordCloudDto : wordCloudDtos) {
            WordCloud wordCloud = new WordCloud();
            wordCloud.setId(wordCloudDto.getId());
            wordCloud.setTitle(wordCloudDto.getTitle());
            wordCloud.setCount(wordCloudDto.getCount());
            wordCloud.setQuestionDocument(questionDocumentRepository.findById(id).get());
            wordCloudList.add(wordCloud);
        }
        wordCloudRepository.deleteAllByQuestionDocument(questionDocumentRepository.findById(id).get());
        questionDocumentRepository.findById(id).get().setWordCloudList(wordCloudList);
        questionDocumentRepository.flush();
    }

    @Transactional
    public void countAnswer(Long id) {
        Optional<SurveyDocument> byId = surveyDocumentRepository.findById(id);
        if (byId.isPresent()) {
            surveyDocumentRepository.incrementCountAnswer(id);
        }
    }

    @Transactional
    public void updateSurvey(HttpServletRequest request,SurveyRequestDto requestDto, Long surveyId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyId).orElseGet(null);

        // User Module 로 부터 현재 유저 정보 가져오기
        User currentUser = apiService.getCurrentUserFromUser(request);

        // 유저 확인 검사
        if(!surveyDocument.getSurvey().getUser().equals(currentUser)) {
            // todo: User 확인 Exception 처리
        }

        updateTest(surveyDocument, requestDto);
    }

    public void updateTest(SurveyDocument surveyDocument, SurveyRequestDto requestDto) {
        // 초기 값 수정
        surveyDocument.setTitle(requestDto.getTitle());
        surveyDocument.setDescription(requestDto.getDescription());
        surveyDocument.setType(requestDto.getType());

        // Question List 수정
        // survey document 의 Question List 초기화
        surveyDocument.getQuestionDocumentList().clear();

        for (QuestionRequestDto questionRequestDto : requestDto.getQuestionRequest()) {
            QuestionDocument question = QuestionDocument.builder()
                    .surveyDocument(surveyDocument)
                    .title(questionRequestDto.getTitle())
                    .questionType(questionRequestDto.getType())
                    .build();
            questionDocumentRepository.save(question);

            if(questionRequestDto.getType() == 0) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            question.setChoiceList(new ArrayList<>());
            for(ChoiceRequestDto choiceRequestDto: questionRequestDto.getChoiceList()) {
                Choice choice = Choice.builder()
                        .question_id(question)
                        .title(choiceRequestDto.getChoiceName())
                        .count(0)
                        .build();
                choiceRepository.save(choice);
                question.setChoice(choice);
            }

            surveyDocument.setQuestion(question);
        }
        surveyDocumentRepository.save(surveyDocument);
    }

    public void deleteSurvey(Long id) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(id).orElseGet(null);
        surveyDocument.setDeleted(true);
        surveyDocumentRepository.save(surveyDocument);
    }

    public void managementSurvey(Long id, DateDto dateRequest) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(id).get();
        surveyDocument.setDate(
                dateRequestToEntity(dateRequest.getStartDate(), dateRequest.getEndDate(), surveyDocument)
        );
        surveyDocumentRepository.save(surveyDocument);
    }

    public SurveyDetailDto getSurveyTemplateDetailDto(Long surveyDocumentId) {
        SurveyTemplate surveyTemplate = surveyTemplateRepository.findById(surveyDocumentId).get();
        SurveyDetailDto surveyDetailDto = new SurveyDetailDto();

        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyDetailDto.setTitle(surveyTemplate.getTitle());
        surveyDetailDto.setDescription(surveyTemplate.getDescription());
        surveyDetailDto.setReliability(surveyTemplate.getReliability());

        DesignTemplate designTemplate = surveyTemplate.getDesignTemplate();
        surveyDetailDto.setFont(designTemplate.getFont());
        surveyDetailDto.setFontSize(designTemplate.getFontSize());
        surveyDetailDto.setBackColor(designTemplate.getBackColor());

        List<QuestionDetailDto> questionDtos = new ArrayList<>();
        for (QuestionTemplate questionTemplate : surveyTemplate.getQuestionTemplateList()) {
            QuestionDetailDto questionDto = new QuestionDetailDto();
            questionDto.setTitle(questionTemplate.getTitle());
            questionDto.setQuestionType(questionTemplate.getQuestionType());


            List<ChoiceDetailDto> choiceDtos = new ArrayList<>();
            if (questionTemplate.getQuestionType() == 0) {
                ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                choiceDto.setTitle(questionTemplate.getTitle());
                choiceDto.setCount(0);

            } else {
                for (ChoiceTemplate choiceTemplate : questionTemplate.getChoiceTemplateList()) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setTitle(choiceTemplate.getTitle());
                    choiceDto.setCount(choiceTemplate.getCount());

                    choiceDtos.add(choiceDto);
                }
            }
            questionDto.setChoiceList(choiceDtos);

//            List<WordCloudDto> wordCloudDtos = new ArrayList<>();
//            for (WordCloud wordCloud : questionDocument.getWordCloudList()) {
//                WordCloudDto wordCloudDto = new WordCloudDto();
//                wordCloudDto.setId(wordCloud.getId());
//                wordCloudDto.setTitle(wordCloud.getTitle());
//                wordCloudDto.setCount(wordCloud.getCount());
//
//                wordCloudDtos.add(wordCloudDto);
//            }
//            questionDto.setWordCloudDtos(wordCloudDtos);

            questionDtos.add(questionDto);
        }

        surveyDetailDto.setQuestionList(questionDtos);

        log.info(String.valueOf(surveyDetailDto));
        return surveyDetailDto;
    }
}
