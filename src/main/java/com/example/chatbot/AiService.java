package com.example.chatbot;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:11434")
            .build();

    public String ask(String question) {
        try {
            System.out.println("AI 요청 시작");

            Map<String, Object> body = Map.of(
                    "model", "llama3.2:3b",
                    "prompt", question,
                    "stream", false,
                    "options", Map.of(
                            "num_predict", 50,
                            "temperature", 0.3
                    )
            );

            Map response = restClient.post()
                    .uri("/api/generate")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            System.out.println("AI 응답 도착");

            Object result = response.get("response");

            if (result == null) {
                return "AI 응답을 가져오지 못했습니다.";
            }

            return result.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 응답이 지연되고 있습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}