package com.example.user.restAPI.service;

import com.example.user.survey.domain.Survey;
import com.example.user.user.domain.User;
import com.example.user.user.exception.UserNotFoundException;
import com.example.user.user.repository.UserRepository;
import com.example.user.user.service.OAuthService;
import com.example.user.user.service.UserService2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestApiUserService {
    private final OAuthService oAuthService;
    private final UserRepository userRepository;
    private final UserService2 userService;

    public User getCurrentUser(HttpServletRequest request) {
        Long userCode = (Long) request.getAttribute("userCode");
        return userRepository.findByUserCode(userCode).orElseThrow(UserNotFoundException::new);
    }

    public void saveSurveyInUser(HttpServletRequest request, Survey survey) {
        User user = userService.getUser(request);
        user.setSurvey(survey);
        userRepository.flush();
    }
}
