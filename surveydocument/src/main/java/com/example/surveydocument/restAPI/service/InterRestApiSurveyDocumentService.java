package com.example.surveydocument.restAPI.service;

import com.example.surveydocument.survey.domain.Survey;
import com.example.surveydocument.survey.domain.SurveyDocument;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.repository.survey.SurveyRepository;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import com.example.surveydocument.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableTransactionManagement
public class InterRestApiSurveyDocumentService {
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final SurveyDocumentService surveyDocumentService;

    public void saveUserInSurvey(User user) {
        Survey survey = Survey.builder()
                .user(user)
                .build();
        surveyRepository.save(survey);
    }

}
