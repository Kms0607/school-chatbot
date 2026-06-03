package com.example.chatbot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TimetableController {

    private final TimetableRepository timetableRepository;
    private final KoreanTranslateUtil koreanTranslateUtil;

    public TimetableController(
            TimetableRepository timetableRepository,
            KoreanTranslateUtil koreanTranslateUtil
    ) {
        this.timetableRepository = timetableRepository;
        this.koreanTranslateUtil = koreanTranslateUtil;
    }

    @GetMapping("/kakao/timetable")
    public Map<String, Object> kakaoTimetable(
            @RequestParam int grade,
            @RequestParam String className,
            @RequestParam(defaultValue = "zh") String lang
    ) {
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
            return kakaoText(grade + "학년 " + className + " 시간표 정보가 없습니다.");
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
        } else {
            sb.append("课程表 - ")
                    .append(grade)
                    .append("年级 ")
                    .append(className)
                    .append("\n\n");
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
            } else {
                sb.append("星期: ").append(vo.getDayOfWeek()).append("\n");
                sb.append("课程: ").append(vo.getSubjectName()).append("\n");
                sb.append("教授: ").append(vo.getProfessorName()).append("\n");
            }

            sb.append("----------------\n");
        }

        return kakaoText(sb.toString());
    }

    @PostMapping("/kakao/timetable")
    public Map<String, Object> kakaoTimetablePost(
            @RequestParam int grade,
            @RequestParam String className,
            @RequestParam(defaultValue = "zh") String lang
    ) {
        return kakaoTimetable(grade, className, lang);
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
}