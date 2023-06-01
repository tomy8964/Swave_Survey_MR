package com.example.user.chatGPT.controller;


import com.example.user.chatGPT.request.ChatGptQuestionRequestDto;
import com.example.user.chatGPT.request.ChatResultDto;
import com.example.user.chatGPT.sevice.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-gpt")
public class ChatGptController {

    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping("/question")
    public ChatResultDto sendQuestion(@RequestBody ChatGptQuestionRequestDto requestDto) {
        return chatGptService.chatGptResult(requestDto);
    }
}