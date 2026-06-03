package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final KoreanTranslateUtil translateUtil;

    public TeacherController(
            TeacherRepository teacherRepository,
            KoreanTranslateUtil translateUtil
    ) {
        this.teacherRepository = teacherRepository;
        this.translateUtil = translateUtil;
    }

    @GetMapping("/teacher")
    public List<TeacherVO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @GetMapping("/teacher/{id}")
    public TeacherVO getTeacherById(@PathVariable Integer id) {
        return teacherRepository.findById(id)
                .map(this::convertToVO)
                .orElse(null);
    }

    @GetMapping("/kakao/teacher/{id}")
    public Map<String, Object> kakaoTeacher(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "zh") String lang
    ) {
        TeacherVO teacher = getTeacherById(id);

        if (teacher == null) {
            return kakaoText("해당 교수 정보를 찾을 수 없습니다.");
        }

        String title = switch (lang) {
            case "en" -> "Teacher Information";
            case "vi" -> "Thông tin giảng viên";
            default -> "教师信息";
        };

        String text =
                title + "\n\n" +
                        "이름: " + teacher.getName() + "\n" +
                        "학과: " + translateUtil.translateSubject(teacher.getDepartment(), lang) + "\n" +
                        "전공: " + translateUtil.translateSubject(teacher.getMajor(), lang);

        if (teacher.getPortrait() != null) {
            text += "\n\n" +
                    "출석: " + teacher.getPortrait().getRollCallLevel() + "\n" +
                    "과제: " + teacher.getPortrait().getHomeworkLevel() + "\n" +
                    "시험: " + teacher.getPortrait().getExamLevel() + "\n" +
                    "위험도: " + teacher.getPortrait().getRiskScore();
        }

        return kakaoText(text);
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

    private String toRollCallText(Integer level) {
        if (level == null) return "정보 없음";
        if (level == 1) return "전혀 출석 안 부름";
        if (level == 2) return "가끔 출석 부름";
        if (level == 3) return "매 시간 출석 체크";
        if (level == 4) return "무작위 불시 출석";
        return "정보 없음";
    }

    private String toHomeworkText(Integer level) {
        if (level == null) return "정보 없음";
        if (level == 1) return "과제 없음";
        if (level == 2) return "소량 과제만 있음";
        if (level == 3) return "매주 과제 나옴";
        if (level == 4) return "발표/레포트 잦음";
        return "정보 없음";
    }

    private String toExamText(Integer level) {
        if (level == null) return "정보 없음";
        if (level == 1) return "학점 관대함";
        if (level == 2) return "보통 난이도";
        if (level == 3) return "시험 어려움";
        if (level == 4) return "낙제율 높음";
        return "정보 없음";
    }

    private String toRiskText(Integer score) {
        if (score == null) return "정보 없음";
        if (score == 1) return "위험 매우 낮음";
        if (score == 2) return "위험 낮음";
        if (score == 3) return "보통 위험";
        if (score == 4) return "위험 높음";
        if (score == 5) return "위험 매우 높음";
        return "정보 없음";
    }

    private TeacherVO convertToVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();

        vo.setTeacherId(teacher.getTeacherId());
        vo.setName(teacher.getName());
        vo.setDepartment(teacher.getDepartment());
        vo.setMajor(teacher.getMajor());
        vo.setCreateTime(teacher.getCreateTime());
        vo.setUpdateTime(teacher.getUpdateTime());

        if (teacher.getPortrait() != null) {
            TeacherVO.PortraitInfo p = new TeacherVO.PortraitInfo();

            p.setRollCallLevel(toRollCallText(teacher.getPortrait().getRollCallLevel()));
            p.setHomeworkLevel(toHomeworkText(teacher.getPortrait().getHomeworkLevel()));
            p.setExamLevel(toExamText(teacher.getPortrait().getExamLevel()));
            p.setAttendanceRatio(teacher.getPortrait().getAttendanceRatio());
            p.setStyleTag(teacher.getPortrait().getStyleTag());
            p.setRiskScore(toRiskText(teacher.getPortrait().getRiskScore()));

            vo.setPortrait(p);
        }

        if (teacher.getReviews() != null) {
            List<TeacherVO.ReviewInfo> reviewList = teacher.getReviews().stream()
                    .map(review -> {
                        TeacherVO.ReviewInfo r = new TeacherVO.ReviewInfo();
                        r.setId(review.getId());
                        r.setContent(review.getContent());
                        r.setCreateTime(review.getCreateTime());
                        r.setIsPass(review.getIsPass());
                        return r;
                    })
                    .collect(Collectors.toList());

            vo.setReviews(reviewList);
        }

        return vo;
    }
}