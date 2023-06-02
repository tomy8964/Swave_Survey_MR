package com.example.surveydocument.survey.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.surveydocument.restAPI.service.RestApiSurveyDocumentService;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.repository.choice.ChoiceRepository;
import com.example.surveydocument.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.surveydocument.survey.repository.survey.SurveyRepository;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.repository.wordCloud.WordCloudRepository;
import com.example.surveydocument.survey.request.ChoiceRequestDto;
import com.example.surveydocument.survey.request.PageRequestDto;
import com.example.surveydocument.survey.request.QuestionRequestDto;
import com.example.surveydocument.survey.request.SurveyRequestDto;
import com.example.surveydocument.survey.response.ChoiceDetailDto;
import com.example.surveydocument.survey.response.QuestionDetailDto;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.response.WordCloudDto;
import com.example.surveydocument.user.domain.User;
import com.example.surveydocument.user.service.UserService2;
import com.example.surveydocument.util.OAuth.JwtProperties;
import com.example.surveydocument.util.page.PageRequest;
import com.example.surveydocument.survey.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static com.example.surveyAnswer.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyDocumentService {
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final QuestionDocumentRepository questionDocumentRepository;
    private final ChoiceRepository choiceRepository;
    private final WordCloudRepository wordCloudRepository;

    private final RestApiSurveyDocumentService apiService;
//    private static String gateway="gateway-service:8080";

    private static String gateway="localhost:8080";

    @Transactional
    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException, UnknownHostException {

        // 유저 정보 받아오기
        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser();
        Survey userSurvey = getUser.getSurvey();

        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(getUser)
                    .surveyDocumentList(new ArrayList<>())
                    .build();
            surveyRepository.save(userSurvey);
        }

        // Survey Request 를 Survey Document 에 저장하기
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .survey(userSurvey)
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .questionDocumentList(new ArrayList<>())
                .surveyAnswerList(new ArrayList<>())
                .countAnswer(0)
                .build();
        surveyDocumentRepository.save(surveyDocument);

        // 설문 문항
        surveyDocumentRepository.findById(surveyDocument.getId());
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

        // User Module 에 저장된 Survey 보내기
        apiService.sendSurveyToUser(userSurvey);
    }

    // gird method 로 SurveyDocument 조회
    public List<SurveyDocument> readSurveyListByGrid(HttpServletRequest request, PageRequestDto pageRequest) {

        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser();

        return surveyRepository.getSurveyDocumentListGrid(getUser, pageRequest);
    }

    // list method 로 SurveyDocument 조회
    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequestDto pageRequest) throws Exception {


        // User Module 에서 현재 유저 가져오기
        User getUser = apiService.getCurrentUserFromUser();

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

    public SurveyDocument getSurveyDocument(Long surveyDocumentId) {
        return surveyDocumentRepository.findById(surveyDocumentId).get();
    }

    //count +1
    //응답자 수 +1
    public void countChoice(Long choiceId) {
        Optional<Choice> findChoice = choiceRepository.findById(choiceId);

        if (findChoice.isPresent()) {
            //todo: querydsl로 변경
            findChoice.get().setCount(findChoice.get().getCount() + 1);
            choiceRepository.flush();
        }
    }

    // SurveyDocument Response 보낼 SurveyDetailDto로 변환하는 메서드
    private SurveyDetailDto getSurveyDetailDto(Long surveyDocumentId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
        SurveyDetailDto surveyDetailDto = new SurveyDetailDto();

        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyDetailDto.setId(surveyDocument.getId());
        surveyDetailDto.setTitle(surveyDocument.getTitle());
        surveyDetailDto.setDescription(surveyDocument.getDescription());

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

    public void countAnswer(Long id) {
        Optional<SurveyDocument> byId = surveyDocumentRepository.findById(id);
        byId.get().setCountAnswer(byId.get().getCountAnswer() + 1);
        surveyDocumentRepository.flush();
    }

    public void updateSurvey(SurveyRequestDto requestDto, Long surveyId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyId).orElseGet(null);

        // User Module 로 부터 현재 유저 정보 가져오기
        User currentUser = apiService.getCurrentUserFromUser();

        // 유저 확인 검사
        if(!surveyDocument.getSurvey().getUser().equals(currentUser)) {
            // todo: User 확인 Exception 처리
        }

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
    }

    public void managementSurvey(Long id) {

    }
}
