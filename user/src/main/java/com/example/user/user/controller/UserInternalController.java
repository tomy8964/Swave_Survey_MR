package com.example.user.user.controller;

import com.example.user.restAPI.service.InterRestApiUserService;
import com.example.user.survey.domain.Survey;
import com.example.user.user.domain.User;
import com.example.user.user.repository.UserRepository;
import com.example.user.user.service.UserService2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Getter
@Setter
@RequestMapping("/user/internal")
@RequiredArgsConstructor
public class UserInternalController {
    private final UserService2 userService;
    private final UserRepository userRepository;
    private final InterRestApiUserService interRestApiUserService;

    @GetMapping("/me")
    public User getCurrentUser(HttpServletRequest request) {
        return interRestApiUserService.getCurrentUser(request);
    }

    @PostMapping("/survey/save")
    public void saveSurveyInUser(HttpServletRequest request, Survey survey) {
        interRestApiUserService.saveSurveyInUser(request, survey);
    }
}
