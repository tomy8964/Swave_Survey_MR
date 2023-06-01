package com.example.user.user.controller;


import com.example.user.restAPI.service.RestApiUserService;
import com.example.user.survey.domain.Survey;
import com.example.user.survey.response.SurveyMyPageDto;
import com.example.user.user.domain.User;
import com.example.user.user.request.UserUpdateRequest;
import com.example.user.user.service.UserService2;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Getter
@Setter
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService2 userService2;
    private final RestApiUserService restUserService;

    // 구글 추가 버전, requestParam으로 provider 받음
    @PostMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code, @RequestParam("provider") String provider) {
        return userService2.getLogin(code,provider);

    }

    @GetMapping("/me")
    public User getCurrentUser(HttpServletRequest request) {
        return restUserService.getCurrentUser(request);
    }

    @PostMapping("/survey/save")
    public void saveSurvey(HttpServletRequest request, @RequestBody Survey survey) {
        restUserService.saveSurveyInUser(request, survey);
    }

//    @GetMapping("/me")
//    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) throws Exception { //(1)
//        return userService2.getCurrentUser(request);
//    }


    @GetMapping("/mypage")
    public List<SurveyMyPageDto> getMyPage(HttpServletRequest request) { //(1)
        return userService2.mySurveyList(request);
    }

    @PostMapping("/updatepage")
    public String updateMyPage(HttpServletRequest request,@RequestBody UserUpdateRequest user) throws ServletException { //(1)
        return userService2.updateMyPage(request,user);
    }
}
