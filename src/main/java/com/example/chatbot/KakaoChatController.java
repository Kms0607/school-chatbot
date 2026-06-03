package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kakao")
public class KakaoChatController {

    private final AiService aiService;

    public KakaoChatController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {

        System.out.println("카카오 요청 전체 = " + request);

        Map<String, Object> userRequest =
                (Map<String, Object>) request.get("userRequest");

        String userMessage = userRequest.get("utterance").toString();

        System.out.println("사용자 발화 = " + userMessage);

        String prompt = """
당신은 영진전문대학교 챗봇입니다.

절대 규칙:
- 한국어만 사용한다.
- 영어를 사용하면 안 된다.
- 중국어를 사용하면 안 된다.
- 일본어를 사용하면 안 된다.
- 베트남어를 사용하면 안 된다.
- 답변은 20자 이내로 짧게 한다.

질문:
%s
""".formatted(userMessage);

        String answer = aiService.ask(prompt);

        System.out.println("AI 원본 답변 = " + answer);

        if (answer == null || answer.isBlank()) {
            answer = "안녕하세요! 무엇을 도와드릴까요?";
        }

        answer = answer
                .replace("<think>", "")
                .replace("</think>", "")
                .replace("\n\n", "\n")
                .trim();

        if (answer.isBlank()) {
            answer = "안녕하세요! 무엇을 도와드릴까요?";
        }

        if (answer.length() > 500) {
            answer = answer.substring(0, 500);
        }

        System.out.println("최종 답변 = " + answer);

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of(
                                        "simpleText", Map.of(
                                                "text", answer
                                        )
                                )
                        )
                )
        );
    }
}