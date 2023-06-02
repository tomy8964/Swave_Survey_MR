package com.example.surveyanalyze.survey.controller;

import groovy.util.logging.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class TestController {

    private final RedissonClient redissonClient;

    @Autowired
    public TestController(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


}
