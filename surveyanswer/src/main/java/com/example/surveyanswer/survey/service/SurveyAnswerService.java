package com.example.surveyanswer.survey.service;

import com.example.surveyanswer.survey.domain.*;
import com.example.surveyanswer.survey.exception.InvalidTokenException;
import com.example.surveyanswer.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.surveyanswer.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.surveyanswer.survey.response.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAnswerService {
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final RestAPIService restAPIService;

    // 설문 응답 참여
    public SurveyDetailDto getParticipantSurvey(Long id){
        return getSurveyDetailDto(id);
    }

    // 설문 응답 저장
    public void createSurveyAnswer(SurveyResponseDto surveyResponse){
        Long surveyDocumentId = surveyResponse.getId();
        // SurveyDocumentId를 통해 어떤 설문인지 가져옴
//        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();

        // Survey Response 를 Survey Answer 에 저장하기
        SurveyAnswer surveyAnswer = SurveyAnswer.builder()
                .surveyDocumentId(surveyDocumentId)
                .title(surveyResponse.getTitle())
                .description(surveyResponse.getDescription())
                .type(surveyResponse.getType())
                .questionAnswerList(new ArrayList<>())
                .build();
        surveyAnswerRepository.save(surveyAnswer);

        // Survey Response 를 Question Answer 에 저장하기
        surveyAnswerRepository.findById(surveyAnswer.getId());
        for (QuestionResponseDto questionResponseDto : surveyResponse.getQuestionResponse()) {
            // Question Answer 에 저장
            // todo: 주관식0 / 찬부식1, 객관식2 구분 저장
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .surveyAnswerId(surveyAnswerRepository.findById(surveyAnswer.getId()).get())
                    .title(questionResponseDto.getTitle())
                    .questionType(questionResponseDto.getType())
                    .checkAnswer(questionResponseDto.getAnswer())
                    .checkAnswerId(questionResponseDto.getAnswerId())
                    .surveyDocumentId(surveyDocumentId)
                    .build();
            questionAnswerRepository.save(questionAnswer);
            // if 찬부식 or 객관식
            // if 주관식 -> checkId에 주관식인 questionId가 들어감
            if(questionResponseDto.getType()!=0){
                //check 한 answer 의 id 값으로 survey document 의 choice 를 찾아서 count ++
                if (questionAnswer.getCheckAnswerId() != null) {
//                    Optional<Choice> findChoice = choiceRepository.findById(questionAnswer.getCheckAnswerId());
    //                Optional<Choice> findChoice = choiceRepository.findByTitle(questionAnswer.getCheckAnswer());
                    restAPIService.giveChoiceIdToCount(questionAnswer.getCheckAnswerId());

//                    if (findChoice.isPresent()) {
//                        //todo: querydsl로 변경
//                        findChoice.get().setCount(findChoice.get().getCount() + 1);
//                        choiceRepository.save(findChoice.get());
//                    }
                }
            }
            surveyAnswer.setQuestion(questionAnswer);
        }
        surveyAnswerRepository.flush();

        //count Answer
        restAPIService.giveDocumentIdtoCountAnswer(surveyDocumentId);
        // 저장된 설문 응답을 Survey 에 연결 및 저장
//        surveyDocument.setAnswer(surveyAnswer);
//        surveyDocumentRepository.flush();

        //REST API to survey analyze controller
        //todo:응답 할때 다시 수정
        restAPIService.restAPItoAnalyzeController(surveyDocumentId);
    }

    // todo : 분석 응답 리스트 불러오기
    public List<SurveyAnswer> readSurveyAnswerList(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        List<SurveyAnswer> surveyAnswerList = surveyAnswerRepository.findSurveyAnswersBySurveyDocumentId(surveyId);

        checkInvalidToken(request);

        return surveyAnswerList;
    }

    public List<QuestionAnswer> getQuestionAnswers(Long questionDocumentId){
        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        List<QuestionAnswer> questionAnswerList = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocumentId);


        return questionAnswerList;
    }

    // 회원 유효성 검사, token 존재하지 않으면 예외처리
    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            log.info("error");
            throw new InvalidTokenException();
        }
        log.info("토큰 체크 완료");
    }

    // SurveyDocument Response 보낼 SurveyDetailDto로 변환하는 메서드
    private SurveyDetailDto getSurveyDetailDto(Long surveyDocumentId) {
//        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
        SurveyDocument surveyDocument = restAPIService.getSurveyDocument(surveyDocumentId);

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
                List<QuestionAnswer> questionAnswersByCheckAnswerId = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocument.getId());
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

    public List<QuestionAnswer> getQuestionAnswerByCheckAnswerId(Long id) {
        return questionAnswerRepository.findQuestionAnswersByCheckAnswerId(id);
    }
}
