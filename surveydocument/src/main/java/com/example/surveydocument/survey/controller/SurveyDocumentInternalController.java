package com.example.surveydocument.survey.controller;

import com.example.surveydocument.restAPI.service.InterRestApiSurveyDocumentService;
import com.example.surveydocument.survey.domain.Choice;
import com.example.surveydocument.survey.domain.QuestionDocument;
import com.example.surveydocument.survey.domain.SurveyDocument;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.request.PageRequestDto;
import com.example.surveydocument.survey.request.SurveyRequestDto;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.response.WordCloudDto;
import com.example.surveydocument.survey.service.SurveyDocumentService;
import com.example.surveydocument.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/internal")
public class SurveyDocumentInternalController {

    private final SurveyDocumentService surveyService;
    private final InterRestApiSurveyDocumentService apiService;

    @GetMapping(value = "/survey-list/{id}")
    public SurveyDetailDto readDetail(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }

    @GetMapping(value = "/getSurveyDocument/{id}")
    public SurveyDocument readDetail(@PathVariable Long id) {
        return surveyService.getSurveyDocument(id);
    }

    @PostMapping(value = "/count/{id}")
    public String countChoice(@PathVariable Long id) {
         surveyService.countChoice(id);
         return "count success";
    }

    @PostMapping(value = "/countAnswer/{id}")
    public String countAnswer(@PathVariable Long id) {
        surveyService.countAnswer(id);
        return "count success";
    }

    @GetMapping(value = "/getChoice/{id}")
    public Choice getChoice(@PathVariable Long id) {
        return surveyService.getChoice(id);
    }

    @GetMapping(value = "/getQuestion/{id}")
    public QuestionDocument getQuestion(@PathVariable Long id) {

        return surveyService.getQuestion(id);
    }

    @GetMapping(value = "/getQuestionByChoiceId/{id}")
    public QuestionDocument getQuestionByChoiceId(@PathVariable Long id) {
        return surveyService.getQuestionByChoiceId(id);
    }

    @PostMapping(value = "/setWordCloud/{id}")
    public void setWordCloud(@PathVariable Long id, @RequestBody List<WordCloudDto> wordCloudList) {
        surveyService.setWordCloud(id, wordCloudList);
    }

    // 유저 정보 저장하기
    @PostMapping(value = "/saveUser")
    public void saveUser(@RequestBody User user) {
        apiService.saveUserInSurvey(user);
    }

}
