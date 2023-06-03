package com.example.surveydocument;

import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import com.example.surveydocument.user.domain.User;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@SpringBootTest
public class RedisTest {
    @Autowired
    SurveyDocumentService documentService;
    @Autowired
    SurveyDocumentRepository documentRepository;
    @Autowired
    WebClient webClient;
    public static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test @DisplayName("유저 모듈에서 현재 유저 정보 가져오기 테스트")
    void restApi_test1() {
        // given
        User user = User.builder()
                .id(0l)
                .email("abc@gachon.ac.kr")
                .nickname("김기현")
                .build();

        // when
        String baseUrl = String.format(
                "http://localhost:%s", mockWebServer.getPort()
        );

        // then
    }

}
