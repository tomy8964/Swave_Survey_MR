package com.example.surveyanalyze.survey.service;


import com.example.surveyanalyze.survey.domain.*;
import com.example.surveyanalyze.survey.repository.aprioriAnlayze.AprioriAnalyzeRepository;
import com.example.surveyanalyze.survey.repository.chiAnlayze.ChiAnalyzeRepository;
import com.example.surveyanalyze.survey.repository.choiceAnalyze.ChoiceAnalyzeRepository;
import com.example.surveyanalyze.survey.repository.compareAnlayze.CompareAnalyzeRepository;
import com.example.surveyanalyze.survey.repository.questionAnlayze.QuestionAnalyzeRepository;
import com.example.surveyanalyze.survey.repository.surveyAnalyze.SurveyAnalyzeRepository;
import com.example.surveyanalyze.survey.restAPI.service.RestAPIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class AnalyzeServiceTest {

    @Autowired SurveyAnalyzeService surveyAnalyzeService;
    @Autowired ChiAnalyzeRepository chiAnalyzeRepository;
    @Autowired CompareAnalyzeRepository compareAnalyzeRepository;
    @Autowired AprioriAnalyzeRepository aprioriAnalyzeRepository;
    @Autowired ChoiceAnalyzeRepository choiceAnalyzeRepository;
    @Autowired QuestionAnalyzeRepository questionAnalyzeRepository;
    @Autowired SurveyAnalyzeRepository surveyAnalyzeRepository;
    @Autowired RestAPIService restAPIService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        // Create Survey Document
        ObjectMapper objectMapper = new ObjectMapper();

        SurveyDocument surveyDocument = createSurveyDocument();
        String survey = objectMapper.writeValueAsString(surveyDocument);

        List<QuestionDocument> questionDocumentList = surveyDocument.getQuestionDocumentList();
        QuestionDocument questionDocument1 = questionDocumentList.get(0);
        List<Choice> choiceList1 = questionDocument1.getChoiceList();
        String choice1 = objectMapper.writeValueAsString(choiceList1.get(0));
        String choice2 = objectMapper.writeValueAsString(choiceList1.get(1));

        QuestionDocument questionDocument2 = questionDocumentList.get(1);
        List<Choice> choiceList2 = questionDocument2.getChoiceList();
        String choice3 = objectMapper.writeValueAsString(choiceList2.get(0));
        String choice4 = objectMapper.writeValueAsString(choiceList2.get(1));

        List<String> choiceStringList = new ArrayList<>();
        choiceStringList.add(choice1);
        choiceStringList.add(choice2);
        choiceStringList.add(choice3);
        choiceStringList.add(choice4);

        List<Choice> choiceList = new ArrayList<>();
        choiceList.add(choiceList1.get(0));
        choiceList.add(choiceList1.get(1));
        choiceList.add(choiceList2.get(0));
        choiceList.add(choiceList2.get(1));

        // Start the MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Configure the MockWebServer's behavior
        // Configure the Dispatcher
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                MockResponse response = new MockResponse();

                // Customize the response based on the request URL
                if (path.startsWith("/api/internal/getChoice/")) {
                    String id = path.substring("/api/internal/getChoice/".length()); // Extract the value of {id}
                    String choice = choiceStringList.get(Integer.parseInt(id) - 1);
                    // Rest of your logic using the extracted id
                    response.setResponseCode(200)
                            .setBody(choice)
                            .addHeader("Content-Type", "application/json");
                } else if (path.startsWith("/api/internal/getSurveyDocument/")) {
                    response.setResponseCode(200)
                            .setBody(survey)
                            .addHeader("Content-Type", "application/json");
                } else if (path.startsWith("/survey/internal/getQuestionAnswerByCheckAnswerId/")) {
                    String id = path.substring("/survey/internal/getQuestionAnswerByCheckAnswerId/".length()); // Extract the value of {id}
                    // Rest of your logic using the extracted id
                    response.setResponseCode(200)
                            .setBody("word Cloud")
                            .addHeader("Content-Type", "application/json");
                } else if (path.startsWith("/api/internal/getQuestionByChoiceId/")) {
                    String id = path.substring("/api/internal/getQuestionByChoiceId/".length()); // Extract the value of {id}
                    Choice choice = choiceList.get(Integer.parseInt(id) - 1);
                    QuestionDocument questionId = choice.getQuestion_id();
                    // Rest of your logic using the extracted id
                    try {
                        response.setResponseCode(200)
                                .setBody(objectMapper.writeValueAsString(questionId))
                                .addHeader("Content-Type", "application/json");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    response.setResponseCode(200);
                }

                return response;
            }
        });

        // Get the base URL of the mock server
        String url = mockWebServer.url("/").toString();
        URI uri = URI.create(url);
        String baseUrl = uri.getHost() + ":" + uri.getPort();

        System.out.println(baseUrl);


        // Initialize other dependencies and the service under test
        MockitoAnnotations.openMocks(this);
        surveyAnalyzeService = new SurveyAnalyzeService(
                chiAnalyzeRepository,
                compareAnalyzeRepository,
                aprioriAnalyzeRepository,
                choiceAnalyzeRepository,
                questionAnalyzeRepository,
                surveyAnalyzeRepository,
                restAPIService);

        // Update the base URL of the service to use the mock server
        restAPIService.setGateway(baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Shutdown the MockWebServer
        mockWebServer.shutdown();
    }

    @Test
    @Transactional
    @DisplayName("Survey Analyze Create Test")
    public void createAnalyzeTest() {
        //given

        //when
        surveyAnalyzeService.analyze("-1");
        SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findById(1L).get();
        //then
        assertEquals(surveyAnalyze.getSurveyDocumentId(),-1l);

        List<QuestionAnalyze> questionAnalyzeList = surveyAnalyze.getQuestionAnalyzeList();
        QuestionAnalyze questionAnalyze1 = questionAnalyzeList.get(0);
        assertEquals(questionAnalyze1.getQuestionTitle(), "객관식");

        List<ChiAnalyze> chiAnalyzeList = questionAnalyze1.getChiAnalyzeList();
        assertEquals(chiAnalyzeList.get(0).getQuestionTitle(),"찬부식");

        QuestionAnalyze questionAnalyze2 = questionAnalyzeList.get(1);
        assertEquals(questionAnalyze2.getQuestionTitle(), "찬관식");
    }

    private static SurveyDocument createSurveyDocument() {
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .title("설문제목")
                .description("설문설명")
                .reliability(false)
                .type(0)
                .questionDocumentList(new ArrayList<>())
                .build();

        List<QuestionDocument> questionDocumentList = new ArrayList<>();

        QuestionDocument questionDocument1 = QuestionDocument.builder()
                .surveyDocument(surveyDocument)
                .questionType(2)
                .title("객관식")
                .choiceList(new ArrayList<>())
                .build();

        List<Choice> choiceList1 = new ArrayList<>();

        Choice choice1 = Choice.builder()
                .title("짜장")
                .question_id(questionDocument1)
                .build();
        choiceList1.add(choice1);
        choice1.setId(1L);

        Choice choice2 = Choice.builder()
                .title("짬뽕")
                .question_id(questionDocument1)
                .build();
        choiceList1.add(choice2);
        choice2.setId(2L);

        questionDocument1.setChoiceList(choiceList1);
        questionDocument1.setId(1L);

        QuestionDocument questionDocument2 = QuestionDocument.builder()
                .surveyDocument(surveyDocument)
                .questionType(2)
                .title("찬부식")
                .choiceList(new ArrayList<>())
                .build();

        List<Choice> choiceList2 = new ArrayList<>();

        Choice choice3 = Choice.builder()
                .title("true")
                .question_id(questionDocument1)
                .build();
        choiceList2.add(choice3);
        choice3.setId(3L);

        Choice choice4 = Choice.builder()
                .title("false")
                .question_id(questionDocument1)
                .build();
        choice4.setId(4L);
        choiceList2.add(choice4);

        questionDocument2.setChoiceList(choiceList2);
        questionDocument1.setId(2L);

        questionDocumentList.add(questionDocument1);
        questionDocumentList.add(questionDocument2);

        surveyDocument.setQuestionDocumentList(questionDocumentList);

        return surveyDocument;
    }
}
