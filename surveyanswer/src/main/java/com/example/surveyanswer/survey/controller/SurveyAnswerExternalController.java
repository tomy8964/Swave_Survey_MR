package com.example.surveyanswer.survey.controller;

import com.example.surveyanswer.survey.domain.QuestionAnswer;
import com.example.surveyanswer.survey.response.SurveyDetailDto;
import com.example.surveyanswer.survey.response.SurveyResponseDto;
import com.example.surveyanswer.survey.service.SurveyAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/external")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnswerExternalController {

    private final SurveyAnswerService surveyService;

    @GetMapping(value = "/test")
    public String test() {

        return"test";
    }

    // 설문 참여
    @GetMapping(value = "/load/{id}")
    public SurveyDetailDto participateSurvey(@PathVariable Long id) {
        return surveyService.getParticipantSurvey(id);
    }

    // 설문 응답 저장
    @PostMapping(value = "/response/create")
    public String createResponse(@RequestBody SurveyResponseDto surveyForm) {
        // 설문 응답 저장
        System.out.println("survey answer");
        surveyService.createSurveyAnswer(surveyForm);
        return "Success";
    }

//    // 설문 응답 조회
//    @GetMapping(value = "/api/response/{id}")
//    public SurveyDetailDto readResponse(@PathVariable Long id){
//        return surveyService.getParticipantSurvey(id);
//    }

}
