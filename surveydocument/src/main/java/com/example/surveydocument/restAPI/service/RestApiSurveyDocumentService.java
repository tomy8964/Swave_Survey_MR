package com.example.surveydocument.restAPI.service;

import com.example.surveydocument.survey.domain.QuestionAnswer;
import com.example.surveydocument.survey.domain.Survey;
import com.example.surveydocument.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serial;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestApiSurveyDocumentService {

//    private static String gateway="gateway-service:8080";
    private static String userInternalUrl = "/user/internal";
    private static String gateway="localhost:8080";
    // Current User 정보 가져오기
    public User getCurrentUserFromUser(HttpServletRequest request) {
        String jwtHeader = ((HttpServletRequest)request).getHeader("Authorization");
        // WebClient 가져오기
        log.info("현재 유저정보를 가져옵니다");
        WebClient webClient = WebClient.create();

        // Current User URL
        String getCurrentUserUrl = "http://" + gateway + userInternalUrl + "/me";

        User getUser = webClient.get()
                .uri(getCurrentUserUrl)
                .header("Authorization", jwtHeader)
                .retrieve()
                .bodyToMono(User.class)
                .blockOptional()
                .orElseGet(null);

        // check log
        log.info("현재 유저의 설문 정보: " + getUser.getNickname());

        return getUser;
    }

    // User 에 Survey 정보 보내기
    public void sendSurveyToUser(HttpServletRequest request,Survey survey) {
        String jwtHeader = ((HttpServletRequest)request).getHeader("Authorization");
        // WebClient 가져오기
        log.info("Survey 정보를 보냅니다");
        WebClient webClient = WebClient.create();

        // Target URL
        String saveSurveyUrl = "http://" + gateway + userInternalUrl + "/survey/save";

        webClient.post()
                .uri(saveSurveyUrl)
                .header("Authorization", jwtHeader)
                .bodyValue(survey)
                .retrieve()
                .bodyToMono(Survey.class)
                .blockOptional();
//                .orElseGet(null);

        log.info(survey.getUser().getNickname() +"에게 정보 보냅니다");
    }

    // Answer Id 값을 통해 Question Answer 불러오기
    public List<QuestionAnswer> getQuestionAnswersByCheckAnswerId(Long id) {
        //REST API로 분석 시작 컨트롤러로 전달
        // Create a WebClient instance
        log.info("GET questionAnswer List by checkAnswerId");
        WebClient webClient = WebClient.create();

        // Define the API URL
        String apiUrl = "http://"+ gateway +"/survey/internal/getQuestionAnswerByCheckAnswerId/"+ id;

        // Make a GET request to the API and retrieve the response
        List<QuestionAnswer> questionAnswerList = webClient.get()
                .uri(apiUrl)
                .header("Authorization", "NotNull")
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        return mapper.readValue(responseBody, new TypeReference<List<QuestionAnswer>>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .blockOptional()
                .orElse(null);

        // Process the response as needed
        System.out.println("Request: " + questionAnswerList);

        return questionAnswerList;
    }
}
