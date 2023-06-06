package com.example.surveydocument;

import com.example.surveydocument.survey.domain.SurveyDocument;
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

    @BeforeEach
    void clean() {
        documentRepository.deleteAll();
    }

    @Test @DisplayName("설문 저장")
    void service_test_1() {

    }
    @Test @DisplayName("설문 수정")
    void service_test_2() {
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .title()
                .description()
                .
                .build();
    }
}
