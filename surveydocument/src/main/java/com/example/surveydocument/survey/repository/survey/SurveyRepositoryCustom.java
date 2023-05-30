package com.example.surveydocument.survey.repository.survey;

import com.example.surveydocument.survey.domain.SurveyDocument;
import com.example.surveydocument.survey.request.PageRequestDto;
import com.example.surveydocument.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyRepositoryCustom {
    Page<SurveyDocument> surveyDocumentPaging(User user, Pageable pageable);
    SurveyDocument surveyDocumentDetail(User userRequest, SurveyDocument surveyDocumentRequest);

    List<SurveyDocument> getSurveyDocumentListGrid(User user, PageRequestDto pageRequest);

}