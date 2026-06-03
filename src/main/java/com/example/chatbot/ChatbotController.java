package com.example.chatbot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatbotController {

    @GetMapping("/")
    public String home() {
        return "카카오 챗봇 서버 실행 성공!";
    }
}