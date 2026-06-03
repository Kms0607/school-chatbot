package com.example.chatbot;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ScheduleChatController {


    private final ScheduleRepository scheduleRepository;
    private final ScheduleTextUtil scheduleTextUtil;

    private final Map<String, UserState> userStates = new HashMap<>();

    public ScheduleChatController(
            ScheduleRepository scheduleRepository,
            ScheduleTextUtil scheduleTextUtil
    ) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleTextUtil = scheduleTextUtil;
    }

    @PostMapping("/kakao/schedule/chat")
    public Map<String, Object> scheduleChat(@RequestBody Map<String, Object> request) {

        Map<String, Object> userRequest = (Map<String, Object>) request.get("userRequest");
        String utterance = String.valueOf(userRequest.get("utterance")).trim();
        Map<String, Object> user = (Map<String, Object>) userRequest.get("user");
        String userId = String.valueOf(user.get("id"));

        UserState state = userStates.getOrDefault(userId, new UserState());


        if (utterance.contains("일정") || utterance.contains("schedule")) {
            state.step = "WAIT_LANG";
            userStates.put(userId, state);
            return kakaoText("언어를 입력하세요.\n예: 한국어 / 중국어 / 영어 / 베트남어");
        }


        if ("WAIT_LANG".equals(state.step)) {
            String lang;
            if (utterance.contains("영어") || utterance.equalsIgnoreCase("en")) {
                lang = "en";
            } else if (utterance.contains("베트남어") || utterance.equalsIgnoreCase("vi")) {
                lang = "vi";
            } else if (utterance.contains("중국어") || utterance.equalsIgnoreCase("zh")) {
                lang = "zh";
            } else if (utterance.contains("한국어") || utterance.equalsIgnoreCase("ko")) {
                lang = "ko";
            } else {
                return kakaoText("언어는 한국어, 중국어, 영어, 베트남어 중에서 입력해주세요.");
            }

            userStates.remove(userId);

            List<Schedule> scheduleList = scheduleRepository.findAll();
            if (scheduleList.isEmpty()) {
                return kakaoText("등록된 학사일정이 없습니다.");
            }


            StringBuilder sb = new StringBuilder();
            if ("en".equals(lang)) {
                sb.append("📅 Academic Schedule\n\n");
            } else if ("vi".equals(lang)) {
                sb.append("📅 Lịch học thuật\n\n");
            } else if ("zh".equals(lang)) {
                sb.append("📅 学术日程\n\n");
            } else {
                sb.append("📅 학사일정\n\n");
            }


            for (Schedule schedule : scheduleList) {
                String title = schedule.getTitle();

                String translatedTitle = scheduleTextUtil.translate(title, lang);

                sb.append(schedule.getStartDate())
                        .append(" ~ ")
                        .append(schedule.getEndDate())
                        .append("\n")
                        .append(translatedTitle)
                        .append("\n------------------------\n");
            }

            return kakaoText(sb.toString());
        }


        return kakaoText("일정 조회를 원하시면 '일정'이라고 입력해주세요.");
    }


    private Map<String, Object> kakaoText(String text) {
        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of("simpleText", Map.of(
                                        "text", text
                                ))
                        )
                )
        );
    }


    static class UserState {
        String step;
    }
}