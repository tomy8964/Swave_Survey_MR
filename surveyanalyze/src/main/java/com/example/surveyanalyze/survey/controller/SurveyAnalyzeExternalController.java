package com.example.surveyanalyze.survey.controller;

import com.example.surveyanalyze.survey.exception.InvalidTokenException;
import com.example.surveyanalyze.survey.response.SurveyAnalyzeDto;
import com.example.surveyanalyze.survey.response.SurveyDetailDto;
import com.example.surveyanalyze.survey.service.SurveyAnalyzeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analyze/external")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnalyzeExternalController {

    private final SurveyAnalyzeService surveyService;

    // 분석 문항 & 응답
    @Cacheable(value = "/research/survey/load/{id}", key = "#id")
    @GetMapping(value = "/research/survey/load/{id}")
    public SurveyDetailDto readSurvey(@PathVariable Long id) {
        return surveyService.getSurveyDetailDto(id);
    }

//    // 분석 응답 조회
//    @Cacheable(value = "response/id", key = "#id")
//    @GetMapping(value = "/response/{id}")
//    public SurveyDetailDto readResponse(@PathVariable Long id) {
//        return surveyService.getSurveyDetailDto(id);
//    }

//    // todo:설문 관리 수정
//    @GetMapping(value = "/api/survey/management/{surveyId}")
//    public SurveyManageDto getManageSurvey(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
//        return surveyService.readSurveyMange(request, surveyId);
//    }
//
//    // todo:설문 관리 조회
//    @PostMapping(value = "/api/survey/management/update/{surveyId}")
//    public String setManageSurvey(HttpServletRequest request,@RequestBody SurveyManageDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
//        surveyService.setSurveyMange(request, surveyId, surveyForm);
//        return "success";
//    }

    // 설문 상세 분석 조회
    @Cacheable(value = "/research/analyze/{id}", key = "#id")
    @GetMapping(value = "/research/analyze/{id}")
    public SurveyAnalyzeDto readDetailAnalyze(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetailAnalyze(request, id);
    }
}
