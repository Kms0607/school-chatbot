package com.example.chatbot;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ScheduleTextUtil {

    private static final Map<String, Map<String, String>> MAP = Map.ofEntries(
            Map.entry("근로자의 날", Map.of(
                    "zh", "劳动节",
                    "en", "Labor Day",
                    "vi", "Ngày Lao động"
            )),
            Map.entry("어린이날", Map.of(
                    "zh", "儿童节",
                    "en", "Children's Day",
                    "vi", "Ngày Thiếu nhi"
            )),
            Map.entry("백호체육대회", Map.of(
                    "zh", "白虎体育大会",
                    "en", "Baekho Sports Festival",
                    "vi", "Hội thể thao Bạch Hổ"
            )),
            Map.entry("개교기념일(49주년)", Map.of(
                    "zh", "建校纪念日(49周年)",
                    "en", "49th School Anniversary",
                    "vi", "Ngày thành lập trường (49 năm)"
            )),
            Map.entry("부처님오신날", Map.of(
                    "zh", "佛诞节",
                    "en", "Buddha's Birthday",
                    "vi", "Ngày Phật Đản"
            )),
            Map.entry("대체휴일(부처님오신날)", Map.of(
                    "zh", "佛诞节补休",
                    "en", "Substitute Holiday",
                    "vi", "Ngày nghỉ bù"
            )),
            Map.entry("수업일수 3/4선", Map.of(
                    "zh", "授课进度3/4",
                    "en", "3/4 Classes Completed",
                    "vi", "Hoàn thành 3/4 tiến độ học"
            )),
            Map.entry("하계 계절수업 기간", Map.of(
                    "zh", "夏季学期",
                    "en", "Summer Session",
                    "vi", "Kỳ học hè"
            )),
            Map.entry("하계방학", Map.of(
                    "zh", "暑假",
                    "en", "Summer Vacation",
                    "vi", "Kỳ nghỉ hè"
            )),
            Map.entry("1학기 기말시험", Map.of(
                    "zh", "第一学期期末考试",
                    "en", "1st Semester Final Exam",
                    "vi", "Kỳ thi cuối kỳ 1"
            )),
            Map.entry("하계 계절수업 수강신청 기간", Map.of(
                    "zh", "夏季学期选课期",
                    "en", "Summer Registration",
                    "vi", "Đăng ký học hè"
            )),
            Map.entry("1학기 보강기간", Map.of(
                    "zh", "第一学期补课期",
                    "en", "Make-up Class Period",
                    "vi", "Thời gian học bù"
            ))
    );

    public String translate(String text, String lang) {
        if (text == null) return null;
        return MAP.getOrDefault(text, Map.of()).getOrDefault(lang, text);
    }
}