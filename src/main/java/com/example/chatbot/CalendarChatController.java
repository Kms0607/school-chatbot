package com.example.chatbot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class CalendarChatController {

    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");
    private final UserSession session;
    private final SchoolCalendar2026Repository repo;
    private final CalendarTranslator translator;
    private final ChatMessage msg;

    public CalendarChatController(UserSession session,
                                  SchoolCalendar2026Repository repo,
                                  CalendarTranslator translator,
                                  ChatMessage msg) {
        this.session = session;
        this.repo = repo;
        this.translator = translator;
        this.msg = msg;
    }

    @PostMapping("/kakao/chat/calendar")
    public Map<String, Object> chat(@RequestBody Map<String, Object> body) {
        Map<String, Object> userRequest = (Map<String, Object>) body.get("userRequest");
        String userId = (String) ((Map<String, Object>) userRequest.get("user")).get("id");
        String inputText = ((String) userRequest.get("utterance")).trim();

        // 触发词：학사일정 → 重置流程，弹出韩语语言选择提示
        if ("학사일정".equals(inputText)) {
            session.getUserStep().put(userId, "LANG");
            session.getUserLang().remove(userId);
            return buildResponse(msg.getLangTip());
        }

        String currentStep = session.getUserStep().getOrDefault(userId, "LANG");
        String userLang = session.getUserLang().getOrDefault(userId, "ko");
        String reply;

        try {
            reply = switch (currentStep) {
                case "LANG" -> handleLang(userId, inputText);
                case "YEAR" -> handleYear(userId, inputText, userLang);
                case "MENU" -> handleMenu(userId, inputText, userLang);
                case "MONTH" -> handleMonth(userId, inputText, userLang);
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            // 任意步骤输入错误 → 清空会话，强制重置
            session.getUserStep().remove(userId);
            session.getUserLang().remove(userId);
            reply = msg.get(userLang, "reset_notice");
        }

        return buildResponse(reply);
    }

    // 处理语言选择（选完后切换对应语言）
    private String handleLang(String userId, String input) {
        String lang;
        switch (input) {
            case "1" -> lang = "ko";
            case "2" -> lang = "zh";
            case "3" -> lang = "en";
            case "4" -> lang = "vi";
            default -> throw new IllegalArgumentException();
        }
        session.getUserLang().put(userId, lang);
        session.getUserStep().put(userId, "YEAR");
        return msg.get(lang, "welcome") + "\n" + msg.get(lang, "year_tip");
    }

    // 处理年份
    private String handleYear(String userId, String input, String lang) {
        if (!"2026".equals(input)) {
            throw new IllegalArgumentException();
        }
        session.getUserStep().put(userId, "MENU");
        return msg.get(lang, "menu_tip");
    }

    // 处理菜单选择
    private String handleMenu(String userId, String input, String lang) {
        if ("1".equals(input)) {
            return buildAllSchedule(lang);
        } else if ("2".equals(input)) {
            session.getUserStep().put(userId, "MONTH");
            return msg.get(lang, "month_tip");
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 处理月份查询
    private String handleMonth(String userId, String input, String lang) {
        int month = Integer.parseInt(input);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException();
        }
        List<SchoolCalendar2026> list = repo.findByMonth(month);
        StringBuilder sb = new StringBuilder("📅 2026-" + month + "월\n");
        for (SchoolCalendar2026 item : list) {
            LocalDate start = item.getStartDate();
            LocalDate end = item.getEndDate();
            if(start.isEqual(end)){
                sb.append(start)
                        .append(" ")
                        .append(translator.translate(item.getTitle(), lang))
                        .append("\n");
            }else{
                sb.append(start)
                        .append("~")
                        .append(end.format(MONTH_DAY))
                        .append(" ")
                        .append(translator.translate(item.getTitle(), lang))
                        .append("\n");
            }
        }
        session.getUserStep().put(userId, "MENU");
        return sb.toString();
    }

    // 拼接全部日程
    private String buildAllSchedule(String lang) {
        List<SchoolCalendar2026> list = repo.findAllByOrderByStartDateAsc();
        var group = list.stream().collect(Collectors.groupingBy(c -> c.getStartDate().getMonthValue()));
        StringBuilder sb = new StringBuilder("📅 2026 전체 일정\n");
        for (int i = 1; i <= 12; i++) {
            if (group.containsKey(i)) {
                sb.append("\n[").append(i).append("월]\n");
                for (SchoolCalendar2026 c : group.get(i)) {
                    LocalDate start = c.getStartDate();
                    LocalDate end = c.getEndDate();
                    if(start.isEqual(end)){
                        sb.append(start)
                                .append(" ")
                                .append(translator.translate(c.getTitle(), lang))
                                .append("\n");
                    }else{
                        sb.append(start)
                                .append("~")
                                .append(end.format(MONTH_DAY)) //结束只留月日
                                .append(" ")
                                .append(translator.translate(c.getTitle(), lang))
                                .append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    // 统一返回格式
    private Map<String, Object> buildResponse(String text) {
        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(Map.of("simpleText", Map.of("text", text)))
                )
        );
    }
}