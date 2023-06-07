package com.example.user.restAPI.service;

import com.example.user.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OuterRestApiUserService {
    private static String surveyDocumentInternalUrl = "/api/document/internal";
    @Value("${gateway-service}")
    private static String gateway;

    // Document 에 유저 정보 보내기
    public void sendUserToSurveyDocument(User user) {
        log.info("Document 에 User 정보를 보냅니다");

        WebClient webClient = WebClient.create();
        String documentUrl = "http://" + gateway + surveyDocumentInternalUrl + "/saveUser";

        webClient.post()
                .uri(documentUrl)
                .bodyValue(user);

        log.info(user.getNickname() + " 정보를 Document에 보냅니다");
    }
}
