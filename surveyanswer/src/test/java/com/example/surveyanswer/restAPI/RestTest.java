package com.example.surveyanswer.restAPI;

import com.example.surveyanswer.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.surveyanswer.survey.service.SurveyAnswerService;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class RestTest {
    @Autowired
    SurveyAnswerService surveyAnswerService;
    @Autowired
    SurveyAnswerRepository surveyAnswerRepository;

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

    @Test
    void test1() {

    }




}
