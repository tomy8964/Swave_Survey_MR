package com.example.surveydocument;

import com.example.surveydocument.restAPI.service.RestApiSurveyDocumentService;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class RestApiTest {
    @Autowired
    SurveyDocumentService surveyDocumentService;
    @Autowired
    SurveyDocumentRepository surveyDocumentRepository;
    @Autowired
    RestApiSurveyDocumentService apiService;

    private static MockWebServer mockWebServer;

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

}
