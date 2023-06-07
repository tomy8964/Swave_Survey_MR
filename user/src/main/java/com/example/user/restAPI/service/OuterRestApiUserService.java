package com.example.user.restAPI.service;

import com.example.user.user.domain.User;
import com.example.user.util.OAuth.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
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
    @Value("${gateway.host}")
    private String gateway;

    // Document 에 유저 정보 보내기
    public void sendUserToSurveyDocument(Long userCode) {
        log.info("Document 에 User 정보를 보냅니다");

//        String jwtHeader = ((HttpServletRequest)request).getHeader(JwtProperties.HEADER_STRING);
        WebClient webClient = WebClient.create();
        String documentUrl = "http://" + gateway + surveyDocumentInternalUrl + "/saveUser";

        webClient.post()
                .uri(documentUrl)
                .header("Authorization","NouNull")
                .bodyValue(String.valueOf(userCode))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(userCode + " 정보를 Document에 보냅니다");
    }
}
