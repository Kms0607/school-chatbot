package com.example.chatbot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CalendarChatController {

    private static final DateTimeFormatter MONTH_DAY =
            DateTimeFormatter.ofPattern("MM-dd");

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

        Map<String, Object> userRequest =
                (Map<String, Object>) body.get("userRequest");

        Map<String, Object> user =
                (Map<String, Object>) userRequest.get("user");

        String userId = String.valueOf(user.get("id"));
        String inputText = String.valueOf(userRequest.get("utterance")).trim();

        String lang = session.getLang(userId);
        String step = session.getStep(userId);

        if ("학사일정".equals(inputText)
                || "Academic Calendar".equalsIgnoreCase(inputText)
                || "calendar".equalsIgnoreCase(inputText)
                || inputText.contains("学校日程")
                || inputText.contains("学事日程")) {

            session.setStep(userId, "CALENDAR_YEAR");
            return buildYearButtonResponse(lang);
        }

        try {
            if ("CALENDAR_YEAR".equals(step)) {
                return handleYear(userId, inputText, lang);
            }

            if ("CALENDAR_MENU".equals(step)) {
                return handleMenu(userId, inputText, lang);
            }

            if ("CALENDAR_MONTH".equals(step)) {
                return buildResponse(handleMonth(userId, inputText, lang));
            }

        } catch (Exception e) {
            session.clearTimetableState(userId);
            return buildResponse(msg.get(lang, "reset_notice"));
        }

        return buildResponse(msg.get(lang, "reset_notice"));
    }

    private Map<String, Object> handleYear(String userId, String input, String lang) {

        if (!"2026".equals(input)) {
            throw new IllegalArgumentException();
        }

        session.setStep(userId, "CALENDAR_MENU");
        return buildMenuButtonResponse(lang);
    }

    private Map<String, Object> handleMenu(String userId, String input, String lang) {

        if ("전체 일정".equals(input)
                || "All Schedule".equalsIgnoreCase(input)
                || "全部日程".equals(input)) {

            session.setStep(userId, null);
            return buildResponse(buildAllSchedule(lang));
        }

        if ("월별 일정".equals(input)
                || "Monthly Schedule".equalsIgnoreCase(input)
                || "按月查看".equals(input)) {

            session.setStep(userId, "CALENDAR_MONTH");
            return buildMonthButtonResponse(lang);
        }

        throw new IllegalArgumentException();
    }

    private String handleMonth(String userId, String input, String lang) {

        input = input
                .replace("월", "")
                .replace("月", "")
                .replace("Month", "")
                .replace("month", "")
                .trim();

        int month = Integer.parseInt(input);

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException();
        }

        List<SchoolCalendar2026> list = repo.findByMonth(month);

        StringBuilder sb = new StringBuilder();

        if ("en".equals(lang)) {
            sb.append("📅 2026-").append(month).append("\n");
        } else if ("zh".equals(lang)) {
            sb.append("📅 2026年 ").append(month).append("月\n");
        } else {
            sb.append("📅 2026년 ").append(month).append("월\n");
        }

        if (list.isEmpty()) {
            sb.append(noScheduleText(lang));
        }

        for (SchoolCalendar2026 item : list) {
            LocalDate start = item.getStartDate();
            LocalDate end = item.getEndDate();

            if (start.isEqual(end)) {
                sb.append(start)
                        .append(" ")
                        .append(translator.translate(item.getTitle(), lang))
                        .append("\n");
            } else {
                sb.append(start)
                        .append("~")
                        .append(end.format(MONTH_DAY))
                        .append(" ")
                        .append(translator.translate(item.getTitle(), lang))
                        .append("\n");
            }
        }

        session.setStep(userId, "CALENDAR_MENU");
        return sb.toString();
    }

    private String buildAllSchedule(String lang) {

        List<SchoolCalendar2026> list =
                repo.findAllByOrderByStartDateAsc();

        StringBuilder sb = new StringBuilder();

        if ("en".equals(lang)) {
            sb.append("📅 Full Academic Schedule 2026\n");
        } else if ("zh".equals(lang)) {
            sb.append("📅 2026年全年学术日程\n");
        } else {
            sb.append("📅 2026 전체 학사일정\n");
        }

        if (list.isEmpty()) {
            sb.append("\n").append(noScheduleText(lang));
            return sb.toString();
        }

        Map<Integer, List<SchoolCalendar2026>> group = list.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getStartDate().getMonthValue()
                ));

        for (int i = 1; i <= 12; i++) {
            if (group.containsKey(i)) {

                if ("en".equals(lang)) {
                    sb.append("\n[").append(i).append("]\n");
                } else if ("zh".equals(lang)) {
                    sb.append("\n[").append(i).append("月]\n");
                } else {
                    sb.append("\n[").append(i).append("월]\n");
                }

                for (SchoolCalendar2026 c : group.get(i)) {
                    LocalDate start = c.getStartDate();
                    LocalDate end = c.getEndDate();

                    if (start.isEqual(end)) {
                        sb.append(start)
                                .append(" ")
                                .append(translator.translate(c.getTitle(), lang))
                                .append("\n");
                    } else {
                        sb.append(start)
                                .append("~")
                                .append(end.format(MONTH_DAY))
                                .append(" ")
                                .append(translator.translate(c.getTitle(), lang))
                                .append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    private Map<String, Object> buildYearButtonResponse(String lang) {

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of("basicCard", Map.of(
                                        "title", msg.get(lang, "welcome"),
                                        "description", msg.get(lang, "year_tip"),
                                        "buttons", List.of(
                                                button("2026", "2026")
                                        )
                                ))
                        )
                )
        );
    }

    private Map<String, Object> buildMenuButtonResponse(String lang) {

        String title;
        String allLabel;
        String monthLabel;

        if ("en".equals(lang)) {
            title = "Select Menu";
            allLabel = "All Schedule";
            monthLabel = "Monthly Schedule";
        } else if ("zh".equals(lang)) {
            title = "请选择菜单";
            allLabel = "全部日程";
            monthLabel = "按月查看";
        } else {
            title = "메뉴를 선택하세요";
            allLabel = "전체 일정";
            monthLabel = "월별 일정";
        }

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of("basicCard", Map.of(
                                        "title", title,
                                        "buttons", List.of(
                                                button(allLabel, allLabel),
                                                button(monthLabel, monthLabel)
                                        )
                                ))
                        )
                )
        );
    }

    private Map<String, Object> buildMonthButtonResponse(String lang) {

        String title;

        if ("en".equals(lang)) {
            title = "Select Month";
        } else if ("zh".equals(lang)) {
            title = "请选择月份";
        } else {
            title = "월을 선택하세요";
        }

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of("basicCard", Map.of(
                                        "title", title,
                                        "description", monthTip(lang),
                                        "buttons", List.of(
                                                monthButton(1, lang),
                                                monthButton(2, lang),
                                                monthButton(3, lang)
                                        )
                                ))
                        )
                )
        );
    }

    private Map<String, Object> monthButton(int month, String lang) {
        String label;

        if ("en".equals(lang)) {
            label = month + " Month";
        } else if ("zh".equals(lang)) {
            label = month + "月";
        } else {
            label = month + "월";
        }

        return button(label, label);
    }

    private String monthTip(String lang) {
        if ("en".equals(lang)) {
            return "Select or enter a month. Example: 4";
        } else if ("zh".equals(lang)) {
            return "请选择或输入月份。例: 4月";
        } else {
            return "원하는 월을 선택하거나 입력하세요. 예: 4월";
        }
    }

    private String noScheduleText(String lang) {
        if ("en".equals(lang)) {
            return "No academic schedule found.";
        } else if ("zh".equals(lang)) {
            return "没有找到学术日程。";
        } else {
            return "등록된 학사일정이 없습니다.";
        }
    }

    private Map<String, Object> button(String label, String messageText) {
        return Map.of(
                "action", "message",
                "label", label,
                "messageText", messageText
        );
    }

    private Map<String, Object> buildResponse(String text) {
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