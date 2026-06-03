package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TimetableChatController {

    private final TimetableRepository timetableRepository;
    private final KoreanTranslateUtil koreanTranslateUtil;

    private final Map<String, UserState> userStates = new HashMap<>();

    public TimetableChatController(
            TimetableRepository timetableRepository,
            KoreanTranslateUtil koreanTranslateUtil
    ) {
        this.timetableRepository = timetableRepository;
        this.koreanTranslateUtil = koreanTranslateUtil;
    }

    @PostMapping("/kakao/timetable/chat")
    public Map<String, Object> timetableChat(@RequestBody Map<String, Object> request) {

        Map<String, Object> userRequest =
                (Map<String, Object>) request.get("userRequest");

        String utterance = String.valueOf(userRequest.get("utterance")).trim();

        Map<String, Object> user =
                (Map<String, Object>) userRequest.get("user");

        String userId = String.valueOf(user.get("id"));

        UserState state = userStates.getOrDefault(userId, new UserState());

        // 1. 처음에 언어 선택 가능
        String selectedLang = detectLang(utterance);
        if (selectedLang != null && state.step == null) {
            state.lang = selectedLang;
            userStates.put(userId, state);
            return kakaoText(msg("LANG_SET", state.lang));
        }

        // 2. 시간표 조회 시작
        if (utterance.contains("시간표")
                || utterance.equalsIgnoreCase("timetable")
                || utterance.contains("课程表")
                || utterance.toLowerCase().contains("thời khóa biểu")) {

            state.step = "WAIT_GRADE";
            userStates.put(userId, state);

            return kakaoText(msg("ASK_GRADE", state.lang));
        }

        // 3. 학년 입력
        if ("WAIT_GRADE".equals(state.step)) {
            try {
                state.grade = Integer.parseInt(utterance);
                state.step = "WAIT_CLASS";
                userStates.put(userId, state);

                return kakaoText(msg("ASK_CLASS", state.lang));

            } catch (Exception e) {
                return kakaoText(msg("GRADE_ERROR", state.lang));
            }
        }

        // 4. 반 입력 후 바로 시간표 출력
        if ("WAIT_CLASS".equals(state.step)) {
            state.className = utterance.endsWith("반")
                    ? utterance
                    : utterance + "반";

            String lang = state.lang;
            int grade = state.grade;
            String className = state.className;

            userStates.remove(userId);

            List<TimetableVo> list = timetableRepository
                    .findByGradeAndClassName(grade, className)
                    .stream()
                    .map(timetable -> TimetableVo.getVo(
                            timetable,
                            koreanTranslateUtil,
                            lang
                    ))
                    .collect(Collectors.toList());

            if (list.isEmpty()) {
                return kakaoText(msg("NO_TIMETABLE", lang));
            }

            StringBuilder sb = new StringBuilder();

            if ("en".equals(lang)) {
                sb.append("Timetable - Grade ")
                        .append(grade)
                        .append(" ")
                        .append(className)
                        .append("\n\n");
            } else if ("vi".equals(lang)) {
                sb.append("Thời khóa biểu - Lớp ")
                        .append(grade)
                        .append(" ")
                        .append(className)
                        .append("\n\n");
            } else if ("zh".equals(lang)) {
                sb.append("课程表 - ")
                        .append(grade)
                        .append("年级 ")
                        .append(className)
                        .append("\n\n");
            } else {
                sb.append(grade)
                        .append("학년 ")
                        .append(className)
                        .append(" 시간표\n\n");
            }

            for (TimetableVo vo : list) {
                if ("en".equals(lang)) {
                    sb.append("Day: ").append(vo.getDayOfWeek()).append("\n");
                    sb.append("Subject: ").append(vo.getSubjectName()).append("\n");
                    sb.append("Professor: ").append(vo.getProfessorName()).append("\n");
                } else if ("vi".equals(lang)) {
                    sb.append("Ngày: ").append(vo.getDayOfWeek()).append("\n");
                    sb.append("Môn học: ").append(vo.getSubjectName()).append("\n");
                    sb.append("Giảng viên: ").append(vo.getProfessorName()).append("\n");
                } else if ("zh".equals(lang)) {
                    sb.append("星期: ").append(vo.getDayOfWeek()).append("\n");
                    sb.append("课程: ").append(vo.getSubjectName()).append("\n");
                    sb.append("教授: ").append(vo.getProfessorName()).append("\n");
                } else {
                    sb.append("요일: ").append(vo.getDayOfWeek()).append("\n");
                    sb.append("과목: ").append(vo.getSubjectName()).append("\n");
                    sb.append("교수: ").append(vo.getProfessorName()).append("\n");
                }

                sb.append("----------------\n");
            }

            return kakaoText(sb.toString());
        }

        // 5. 기본 안내
        return kakaoText(msg("HELP", state.lang));
    }

    private String detectLang(String utterance) {
        if (utterance.contains("한국") || utterance.equalsIgnoreCase("ko")) {
            return "ko";
        }

        if (utterance.contains("영어") || utterance.equalsIgnoreCase("en")) {
            return "en";
        }

        if (utterance.contains("중국") || utterance.equalsIgnoreCase("zh")) {
            return "zh";
        }

        if (utterance.contains("베트남") || utterance.equalsIgnoreCase("vi")) {
            return "vi";
        }

        return null;
    }

    private String msg(String key, String lang) {
        if (lang == null) {
            lang = "ko";
        }

        if ("en".equals(lang)) {
            switch (key) {
                case "LANG_SET":
                    return "Language has been set to English.\nHow can I help you?";
                case "ASK_GRADE":
                    return "Please enter your grade.\nExample: 1";
                case "ASK_CLASS":
                    return "Please enter your class.\nExample: A";
                case "GRADE_ERROR":
                    return "Please enter the grade as a number.\nExample: 1";
                case "NO_TIMETABLE":
                    return "No timetable found.";
                default:
                    return "How can I help you?\nYou can ask about the timetable.";
            }
        }

        if ("zh".equals(lang)) {
            switch (key) {
                case "LANG_SET":
                    return "语言已设置为中文。\n请问需要什么帮助？";
                case "ASK_GRADE":
                    return "请输入年级。\n例: 1";
                case "ASK_CLASS":
                    return "请输入班级。\n例: A";
                case "GRADE_ERROR":
                    return "年级请输入数字。\n例: 1";
                case "NO_TIMETABLE":
                    return "没有找到课程表。";
                default:
                    return "请问需要什么帮助？\n可以查询课程表。";
            }
        }

        if ("vi".equals(lang)) {
            switch (key) {
                case "LANG_SET":
                    return "Ngôn ngữ đã được đặt thành tiếng Việt.\nTôi có thể giúp gì cho bạn?";
                case "ASK_GRADE":
                    return "Vui lòng nhập khối lớp.\nVí dụ: 1";
                case "ASK_CLASS":
                    return "Vui lòng nhập lớp.\nVí dụ: A";
                case "GRADE_ERROR":
                    return "Vui lòng nhập khối lớp bằng số.\nVí dụ: 1";
                case "NO_TIMETABLE":
                    return "Không tìm thấy thời khóa biểu.";
                default:
                    return "Tôi có thể giúp gì cho bạn?\nBạn có thể hỏi về thời khóa biểu.";
            }
        }

        switch (key) {
            case "LANG_SET":
                return "한국어로 설정되었습니다.\n무엇을 도와드릴까요?";
            case "ASK_GRADE":
                return "학년을 입력하세요.\n예: 1";
            case "ASK_CLASS":
                return "반을 입력하세요.\n예: A";
            case "GRADE_ERROR":
                return "학년은 숫자로 입력하세요.\n예: 1";
            case "NO_TIMETABLE":
                return "시간표가 없습니다.";
            default:
                return "무엇을 도와드릴까요?\n시간표 조회를 원하면 '시간표'라고 입력하세요.";
        }
    }

    private Map<String, Object> kakaoText(String text) {
        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of(
                                        "simpleText", Map.of(
                                                "text", text
                                        )
                                )
                        )
                )
        );
    }

    static class UserState {
        String step;
        String lang = "ko";
        int grade;
        String className;
    }
}