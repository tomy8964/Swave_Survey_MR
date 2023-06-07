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
import org.redisson.RedissonRedLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private final RedissonClient redissonClient;

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
        RedissonRedLock lock = new RedissonRedLock(redissonClient.getLock("/research/analyze/create"));

        try {
            if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                // transaction
                 return userService.updateMyPage(request,user);
            } else {
                throw new RuntimeException("Failed to acquire lock.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @PostMapping("/deleteuser")
    public String deleteUs(HttpServletRequest request) {
        userService.deleteUser(request);
        return "success";
    }

}
