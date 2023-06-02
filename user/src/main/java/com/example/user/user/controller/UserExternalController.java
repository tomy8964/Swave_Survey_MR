package com.example.user.user.controller;

import com.example.user.restAPI.service.RestApiUserService;
import com.example.user.survey.domain.Survey;
import com.example.user.survey.response.SurveyMyPageDto;
import com.example.user.user.domain.User;
import com.example.user.user.repository.UserRepository;
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
@RequestMapping("/user/external")
@RequiredArgsConstructor
public class UserExternalController {
    private final UserService2 userService;
    private final RestApiUserService restApiService;
    private final UserRepository userRepository;

    @PostMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code, @RequestParam("provider") String provider) {
        return userService.getLogin(code,provider);
    }

    @GetMapping("/me")
    public User getCurrentUser(HttpServletRequest request) {
        return userService.getCurrentUser(request);
    }

    @GetMapping("/mypage")
    public List<SurveyMyPageDto> getMyPage(HttpServletRequest request) { //(1)
        return userService.mySurveyList(request);
    }

    @PostMapping("/updatepage")
    public String updateMyPage(HttpServletRequest request,@RequestBody UserUpdateRequest user) throws ServletException { //(1)
        return userService.updateMyPage(request,user);
    }

}
