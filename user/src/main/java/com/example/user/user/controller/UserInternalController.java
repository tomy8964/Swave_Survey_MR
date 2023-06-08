package com.example.user.user.controller;

import com.example.user.restAPI.service.InterRestApiUserService;
import com.example.user.survey.domain.Survey;
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
import org.redisson.RedissonRedLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Getter
@Setter
@RequestMapping("/api/user/internal")
@RequiredArgsConstructor
public class UserInternalController {
    private final UserService2 userService;
    private final UserRepository userRepository;
    private final InterRestApiUserService interRestApiUserService;
    private final RedissonClient redissonClient;

    @GetMapping("/me")
    public Long getCurrentUser(HttpServletRequest request) {
        return interRestApiUserService.getCurrentUser(request);
    }

    @PostMapping("/survey/save")
    public void saveSurveyInUser(HttpServletRequest request, Survey survey) {

//        RedissonRedLock lock = new RedissonRedLock(redissonClient.getLock("/survey/save"));
//
//        try {
//            if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
//                // transaction
//                interRestApiUserService.saveSurveyInUser(request, survey);
//            } else {
//                throw new RuntimeException("Failed to acquire lock.");
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            lock.unlock();
//        }
    }
}
