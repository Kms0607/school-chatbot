package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TimetableChatController {


    private final UserSession userSession;
    private final TimetableRepository timetableRepository;
    private final KoreanTranslateUtil koreanTranslateUtil;

    public TimetableChatController(
            TimetableRepository timetableRepository,
            KoreanTranslateUtil koreanTranslateUtil,
            UserSession userSession
    ) {
        this.timetableRepository = timetableRepository;
        this.koreanTranslateUtil = koreanTranslateUtil;
        this.userSession = userSession;
    }

    @PostMapping("/kakao/timetable/chat")
    public Map<String, Object> timetableChat(@RequestBody Map<String, Object> request) {

        Map<String, Object> userRequest =
                (Map<String, Object>) request.get("userRequest");

        String utterance = String.valueOf(userRequest.get("utterance")).trim();

        Map<String, Object> user =
                (Map<String, Object>) userRequest.get("user");

        String userId = String.valueOf(user.get("id"));

        String lang = userSession.getLang(userId);
        String step = userSession.getStep(userId);

        String selectedLang = detectLang(utterance);
        if (selectedLang != null) {
            userSession.setLang(userId, selectedLang);
            return kakaoText(msg("LANG_SET", selectedLang));
        }

        if (utterance.contains("시간표")
                || utterance.equalsIgnoreCase("timetable")
                || utterance.contains("课程表")
                || utterance.toLowerCase().contains("thời khóa biểu")) {

            userSession.setStep(userId, "WAIT_GRADE");
            return kakaoGradeCard(msg("ASK_GRADE", lang), lang);
        }

        if ("WAIT_GRADE".equals(step)) {

            String gradeText = utterance
                    .replace("학년", "")
                    .replace("年级", "")
                    .replace("Grade", "")
                    .replace("grade", "")
                    .trim();

            try {
                int grade = Integer.parseInt(gradeText);

                userSession.setYear(userId, grade);
                userSession.setStep(userId, "WAIT_CLASS");

                return kakaoText(msg("ASK_CLASS", lang));

            } catch (Exception e) {
                return kakaoGradeCard(msg("GRADE_ERROR", lang), lang);
            }
        }

        if ("WAIT_CLASS".equals(step)) {

            String className = utterance.endsWith("반")
                    ? utterance
                    : utterance + "반";

            userSession.setClassName(userId, className);

            int grade = userSession.getYear(userId);

            userSession.clearTimetableState(userId);

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

        return kakaoText(msg("HELP", lang));
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
                    return "Please select your grade.";
                case "ASK_CLASS":
                    return "Please enter your class.\nExample: A";
                case "GRADE_ERROR":
                    return "Please select 1st grade, 2nd grade, or 3rd grade.";
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
                    return "请选择年级。";
                case "ASK_CLASS":
                    return "请输入班级。\n例: A";
                case "GRADE_ERROR":
                    return "请选择1年级、2年级或3年级。";
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
                    return "Vui lòng chọn khối lớp.";
                case "ASK_CLASS":
                    return "Vui lòng nhập lớp.\nVí dụ: A";
                case "GRADE_ERROR":
                    return "Vui lòng chọn khối 1, 2 hoặc 3.";
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
                return "학년을 선택하세요.";
            case "ASK_CLASS":
                return "반을 입력하세요.\n예: A";
            case "GRADE_ERROR":
                return "1학년, 2학년, 3학년 중에서 선택하세요.";
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

    private Map<String, Object> kakaoGradeCard(String text, String lang) {

        String title = switch (lang) {
            case "zh" -> "课程表查询";
            case "en" -> "Timetable Search";
            case "vi" -> "Tra cứu thời khóa biểu";
            default -> "시간표 조회";
        };

        String grade1Label = switch (lang) {
            case "zh" -> "1年级";
            case "en" -> "Grade 1";
            case "vi" -> "Khối 1";
            default -> "1학년";
        };

        String grade2Label = switch (lang) {
            case "zh" -> "2年级";
            case "en" -> "Grade 2";
            case "vi" -> "Khối 2";
            default -> "2학년";
        };

        String grade3Label = switch (lang) {
            case "zh" -> "3年级";
            case "en" -> "Grade 3";
            case "vi" -> "Khối 3";
            default -> "3학년";
        };

        Map<String, Object> grade1 = Map.of(
                "action", "message",
                "label", grade1Label,
                "messageText", grade1Label
        );

        Map<String, Object> grade2 = Map.of(
                "action", "message",
                "label", grade2Label,
                "messageText", grade2Label
        );

        Map<String, Object> grade3 = Map.of(
                "action", "message",
                "label", grade3Label,
                "messageText", grade3Label
        );

        return Map.of(
                "version", "2.0",
                "template", Map.of(
                        "outputs", List.of(
                                Map.of(
                                        "basicCard", Map.of(
                                                "title", title,
                                                "description", text,
                                                "buttons", List.of(
                                                        grade1,
                                                        grade2,
                                                        grade3
                                                )
                                        )
                                )
                        )
                )
        );
    }

}
