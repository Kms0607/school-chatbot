package com.example.chatbot;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserSession {
    private final Map<String, String> userLang = new HashMap<>();
    private final Map<String, String> userStep = new HashMap<>();
    private final Map<String, Integer> userYear = new HashMap<>();

    public Map<String, String> getUserLang() {
        return userLang;
    }

    public Map<String, String> getUserStep() {
        return userStep;
    }

    public Map<String, Integer> getUserYear() {
        return userYear;
    }
}