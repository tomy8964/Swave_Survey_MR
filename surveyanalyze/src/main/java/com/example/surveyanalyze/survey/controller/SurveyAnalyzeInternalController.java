package com.example.surveyanalyze.survey.controller;

import com.example.surveyanalyze.survey.exception.InvalidTokenException;
import com.example.surveyanalyze.survey.response.SurveyAnalyzeDto;
import com.example.surveyanalyze.survey.response.SurveyDetailDto;
import com.example.surveyanalyze.survey.service.SurveyAnalyzeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analyze/internal")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnalyzeInternalController {

    private final SurveyAnalyzeService surveyService;

    @GetMapping(value = "/test")
    public String test() {

        return"test";
    }

    // 설문 분석 시작
    @PostMapping(value = "/research/analyze/create")
    public String saveAnalyze(@RequestBody String surveyId) {
        // 설문 분석 -> 저장 (python)
        surveyService.analyze(surveyId);
        // 주관식 분석 -> 저장
        surveyService.wordCloud(surveyId);
        return "Success";
    }
}
