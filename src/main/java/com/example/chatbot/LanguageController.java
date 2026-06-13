package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LanguageController {


    private final UserSession userSession;

    public LanguageController(UserSession userSession) {
        this.userSession = userSession;
    }

    @PostMapping("/kakao/lang")
    public Map<String, Object> language(@RequestBody Map<String, Object> request) {

        Map<String, Object> userRequest =
                (Map<String, Object>) request.get("userRequest");

        String utterance = "";
        String userId = "defaultUser";

        if (userRequest != null) {
            utterance = String.valueOf(userRequest.getOrDefault("utterance", "")).trim();

            Object userObj = userRequest.get("user");
            if (userObj instanceof Map) {
                Map<String, Object> user = (Map<String, Object>) userObj;
                Object idObj = user.get("id");
                if (idObj != null) {
                    userId = String.valueOf(idObj);
                }
            }
        }

        String lang = switch (utterance) {
            case "한국어", "KR 한국어", "🇰🇷 한국어", "ko" -> "ko";
            case "중국어", "CN 中文", "🇨🇳 中文", "zh", "中文" -> "zh";
            case "영어", "US English", "🇺🇸 English", "en", "English" -> "en";
            default -> null;
        };

        if (lang == null) {
            return langButtons();
        }

        userSession.setLang(userId, lang);

        return kakaoText(switch (lang) {
            case "zh" -> "语言已设置为中文。";
            case "en" -> "Language has been set to English.";
            default -> "언어가 한국어로 설정되었습니다.";
        });
    }

    private Map<String, Object> langButtons() {

        Map<String, Object> kr = Map.of(
                "action", "message",
                "label", "🇰🇷 한국어",
                "messageText", "🇰🇷 한국어"
        );

        Map<String, Object> en = Map.of(
                "action", "message",
                "label", "🇺🇸 English",
                "messageText", "🇺🇸 English"
        );

        Map<String, Object> zh = Map.of(
                "action", "message",
                "label", "🇨🇳 中文",
                "messageText", "🇨🇳 中文"
        );

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of(
                                        "basicCard", Map.of(
                                                "title", "🌐 Language Selection / 언어 선택 / 语言选择",
                                                "description", "사용할 언어를 선택해주세요.",
                                                "buttons", List.of(kr, en, zh)
                                        )
                                )
                        )
                )
        );
    }

    private Map<String, Object> kakaoText(String text) {
        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of("simpleText", Map.of("text", text))
                        )
                )
        );
    }


}
