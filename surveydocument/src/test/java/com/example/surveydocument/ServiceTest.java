package com.example.surveydocument;

import com.example.surveydocument.restAPI.WebClientConfig;
import com.example.surveydocument.restAPI.service.RestApiSurveyDocumentService;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import com.example.surveydocument.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class ServiceTest {
    @Autowired
    SurveyDocumentService documentService;
    @Autowired
    SurveyDocumentRepository documentRepository;
    @Autowired
    WebClient webClient;

    @BeforeEach
    void clean() {
        documentRepository.deleteAll();
        User user = User.builder()
                .id(0l)
                .email("gachon@gachon.ac.kr")
                .nickname("김기현")
                .build();

        // WebClient 사용하기 위한 빈 등록
        this.webClient = webClient;

    }

    @Test @DisplayName("설문 저장")
    void service_test_1() {

    }
}
