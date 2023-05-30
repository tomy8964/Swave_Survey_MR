package com.example.surveydocument.survey.controller;

import com.example.surveydocument.survey.domain.Choice;
import com.example.surveydocument.survey.domain.QuestionDocument;
import com.example.surveydocument.survey.domain.SurveyDocument;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.request.PageRequestDto;
import com.example.surveydocument.survey.request.SurveyRequestDto;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.response.WordCloudDto;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/external")
public class SurveyDocumentExternalController {

    private final SurveyDocumentService surveyService;

    @PostMapping(value = "/create")
    public String create(HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws InvalidTokenException, UnknownHostException {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    // grid 로 조회
    @PostMapping(value = "/survey-list-grid")
    public List<SurveyDocument> readListGrid(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) {
        return surveyService.readSurveyListByGrid(request, pageRequest);
    }

    // list 로 조회
    @PostMapping(value = "/survey-list")
    public Page<SurveyDocument> readList(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) throws Exception {
        return surveyService.readSurveyList(request, pageRequest);
    }

    @GetMapping(value = "/survey-list/{id}")
    public SurveyDetailDto readDetail(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }

}
