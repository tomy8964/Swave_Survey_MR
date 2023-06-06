package com.example.surveyanswer.survey;

import com.example.surveyanswer.survey.domain.QuestionAnswer;
import com.example.surveyanswer.survey.domain.SurveyAnswer;
import com.example.surveyanswer.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.surveyanswer.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.surveyanswer.survey.response.QuestionResponseDto;
import com.example.surveyanswer.survey.response.SurveyResponseDto;
import com.example.surveyanswer.survey.restAPI.service.RestAPIService;
import com.example.surveyanswer.survey.service.SurveyAnswerService;
import groovy.util.logging.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ServiceTest {

    @Autowired
    SurveyAnswerService surveyAnswerService;
    @Autowired
    SurveyAnswerRepository surveyAnswerRepository;
    @Autowired
    QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    RestAPIService restAPIService;

//    @Mock
//    private RecordedRequest recordedRequest;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        // Start the MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Configure the MockWebServer's behavior
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody("Mocked Response");
        mockWebServer.enqueue(mockResponse);

        // Get the base URL of the mock server
        String url = mockWebServer.url("/").toString();
        URI uri = URI.create(url);
        String baseUrl = uri.getHost() + ":" + uri.getPort();

        System.out.println(baseUrl);


        // Initialize other dependencies and the service under test
        MockitoAnnotations.openMocks(this);
        surveyAnswerService = new SurveyAnswerService(surveyAnswerRepository, questionAnswerRepository, restAPIService);

        // Update the base URL of the service to use the mock server
        restAPIService.setGateway(baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Shutdown the MockWebServer
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("설문 응답 저장 테스트")
    void createAnswerTest() {
        //given
        QuestionResponseDto questionResponseDto1 = QuestionResponseDto.builder()
                .title("Question 1")
                .answer("Answer 1")
                .type(2)
                .build();
        QuestionResponseDto questionResponseDto2 = QuestionResponseDto.builder()
                .title("Question 2")
                .answer("Answer 2")
                .type(0)
                .build();

        List<QuestionResponseDto> questionResponseDtoList = new ArrayList<>();
        questionResponseDtoList.add(questionResponseDto1);
        questionResponseDtoList.add(questionResponseDto2);

        SurveyResponseDto surveyResponseDto = SurveyResponseDto.builder()
                .title("설문 제목")
                .description("설문 설명")
                .reliability(false)
                .questionResponse(new ArrayList<>())
                .build();

        surveyResponseDto.setQuestionResponse(questionResponseDtoList);

        //when
        surveyAnswerService.createSurveyAnswer(surveyResponseDto);

        //then
        List<SurveyAnswer> surveyAnswersBySurveyDocumentId = surveyAnswerRepository.findSurveyAnswersBySurveyDocumentId(1L);
        for (SurveyAnswer surveyAnswer : surveyAnswersBySurveyDocumentId) {
            assertEquals(surveyAnswer.getSurveyDocumentId(), 1L);
            assertEquals(surveyAnswer.getTitle(), "설문 제목");
            assertEquals(surveyAnswer.getType(), "설문 설명");
            for (QuestionAnswer questionAnswer : surveyAnswer.getQuestionanswersList()) {
                assertEquals(questionAnswer.getSurveyDocumentId(), 1L);
                assertEquals(questionAnswer.getSurveyAnswerId(), surveyAnswer.getId());
            }
        }
    }
}
