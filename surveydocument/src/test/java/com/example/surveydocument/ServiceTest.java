package com.example.surveydocument;

import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class ServiceTest {
    @Autowired
    SurveyDocumentService documentService;
    @Autowired
    SurveyDocumentRepository documentRepository;

    private static MockWebServer mockWebServer;
    String host;

    @BeforeAll
    static void startApiServer() throws IOException {
        // 가짜 api server 만들기
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutApiServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        host = String.format(
                "http://localhost:%s", mockWebServer.getPort()
        );
    }

    @Test @DisplayName("설문 저장")
    void service_test_1() {
//        SurveyDocument surveyDocument = SurveyDocument.builder()
//                .survey()
//                .title()
//                .description()
//                .countAnswer()
//                .build();
    }
}
