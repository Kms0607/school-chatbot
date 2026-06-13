package com.example.chatbot;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserSession {
    private final Map<String, String> userLang = new HashMap<>();
    private final Map<String, String> userStep = new HashMap<>();
    private final Map<String, Integer> userYear = new HashMap<>();
    private final Map<String, String> userClassName = new HashMap<>();

    public void setLang(String userId, String lang) {
        userLang.put(userId, lang);
    }

    public String getLang(String userId) {
        return userLang.getOrDefault(userId, "ko");
    }

    public void setStep(String userId, String step) {
        if (step == null) {
            userStep.remove(userId);
        } else {
            userStep.put(userId, step);
        }
    }

    public String getStep(String userId) {
        return userStep.get(userId);
    }

    public void setYear(String userId, int year) {
        userYear.put(userId, year);
    }

    public Integer getYear(String userId) {
        return userYear.get(userId);
    }

    public void clearTimetableState(String userId) {
        userStep.remove(userId);
        userYear.remove(userId);
    }
    public void setClassName(String userId, String className) {
        userClassName.put(userId, className);
    }

    public String getClassName(String userId) {
        return userClassName.get(userId);
    }

}