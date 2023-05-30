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
@RequestMapping("/survey/internal")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnswerInternalController {

    private final SurveyAnswerService surveyService;

    // getQuestionAnswer
    @GetMapping(value = "/question/list/{id}")
    public List<QuestionAnswer> getQuestionAnswers(@PathVariable Long id){
        return surveyService.getQuestionAnswers(id);
    }

    // getQuestionAnswerByCheckAnswerId
    @GetMapping(value = "/getQuestionAnswerByCheckAnswerId/{id}")
    public List<QuestionAnswer> getQuestionAnswerByCheckAnswerId(@PathVariable Long id){
        return surveyService.getQuestionAnswerByCheckAnswerId(id);
    }


}
