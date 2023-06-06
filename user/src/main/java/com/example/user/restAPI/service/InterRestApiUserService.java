package com.example.user.restAPI.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.user.survey.domain.Survey;
import com.example.user.user.domain.User;
import com.example.user.user.repository.UserRepository;
import com.example.user.user.service.OAuthService;
import com.example.user.user.service.UserService2;
import com.example.user.util.OAuth.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterRestApiUserService {
    private final OAuthService oAuthService;
    private final UserRepository userRepository;
    private final UserService2 userService;

    public User getCurrentUser(HttpServletRequest request) {
        Long userCode = (Long) request.getAttribute("userCode");

        String jwtHeader = ((HttpServletRequest)request).getHeader(JwtProperties.HEADER_STRING);
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

        userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token).getClaim("id").asLong();
        return userRepository.findByUserCode(userCode).orElseGet(null);
    }

    public void saveSurveyInUser(HttpServletRequest request, Survey survey) {
        User user = userService.getUser(request);
        user.setSurvey(survey);
        userRepository.flush();
    }
}
