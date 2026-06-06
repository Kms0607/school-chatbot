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

    @RequestMapping(value = "/kakao/teacher/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> kakaoTeacher(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "zh") String lang
    ) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);

        if (teacher == null) {
            return kakaoText(label("notFound", lang));
        }

        return kakaoTeacherResponse(teacher, lang);
    }

    @RequestMapping(value = "/kakao/teacher/name", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> kakaoTeacherByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "zh") String lang
    ) {
        Teacher teacher = teacherRepository.findByName(name).orElse(null);

        if (teacher == null) {
            return kakaoText(label("notFound", lang));
        }

        return kakaoTeacherResponse(teacher, lang);
    }

    private Map<String, Object> kakaoTeacherResponse(Teacher teacher, String lang) {
        StringBuilder text = new StringBuilder();

        text.append(label("title", lang)).append("\n\n");
        text.append(label("name", lang)).append(": ")
                .append(teacher.getName()).append("\n");
        text.append(label("department", lang)).append(": ")
                .append(translateUtil.translateSubject(teacher.getDepartment(), lang)).append("\n");
        text.append(label("major", lang)).append(": ")
                .append(translateUtil.translateSubject(teacher.getMajor(), lang));

        if (teacher.getPortrait() != null) {
            text.append("\n\n");
            text.append(label("rollCall", lang)).append(": ")
                    .append(toRollCallText(teacher.getPortrait().getRollCallLevel(), lang)).append("\n");
            text.append(label("homework", lang)).append(": ")
                    .append(toHomeworkText(teacher.getPortrait().getHomeworkLevel(), lang)).append("\n");
            text.append(label("exam", lang)).append(": ")
                    .append(toExamText(teacher.getPortrait().getExamLevel(), lang)).append("\n");
            text.append(label("risk", lang)).append(": ")
                    .append(toRiskText(teacher.getPortrait().getRiskScore(), lang));
        }

        return kakaoText(text.toString());
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

    private String label(String key, String lang) {
        return switch (key) {
            case "title" -> switch (lang) {
                case "en" -> "Teacher Information";
                case "vi" -> "Thông tin giảng viên";
                case "ko" -> "교수 정보";
                default -> "教师信息";
            };
            case "name" -> switch (lang) {
                case "en" -> "Name";
                case "vi" -> "Tên";
                case "ko" -> "이름";
                default -> "姓名";
            };
            case "department" -> switch (lang) {
                case "en" -> "Department";
                case "vi" -> "Khoa";
                case "ko" -> "학과";
                default -> "系";
            };
            case "major" -> switch (lang) {
                case "en" -> "Major";
                case "vi" -> "Chuyên ngành";
                case "ko" -> "전공";
                default -> "专业";
            };
            case "rollCall" -> switch (lang) {
                case "en" -> "Attendance Check";
                case "vi" -> "Điểm danh";
                case "ko" -> "출석";
                default -> "点名";
            };
            case "homework" -> switch (lang) {
                case "en" -> "Assignment";
                case "vi" -> "Bài tập";
                case "ko" -> "과제";
                default -> "作业";
            };
            case "exam" -> switch (lang) {
                case "en" -> "Exam";
                case "vi" -> "Thi";
                case "ko" -> "시험";
                default -> "考试";
            };
            case "risk" -> switch (lang) {
                case "en" -> "Risk Level";
                case "vi" -> "Mức độ rủi ro";
                case "ko" -> "위험도";
                default -> "危险度";
            };
            case "notFound" -> switch (lang) {
                case "en" -> "Teacher information not found.";
                case "vi" -> "Không tìm thấy thông tin giảng viên.";
                case "ko" -> "해당 교수 정보를 찾을 수 없습니다.";
                default -> "找不到该教师信息。";
            };
            default -> "";
        };
    }

    private String toRollCallText(Integer level, String lang) {
        if (level == null) return noInfo(lang);

        return switch (level) {
            case 1 -> switch (lang) {
                case "en" -> "Rarely checks attendance";
                case "vi" -> "Hầu như không điểm danh";
                case "ko" -> "전혀 출석 안 부름";
                default -> "几乎不点名";
            };
            case 2 -> switch (lang) {
                case "en" -> "Sometimes checks attendance";
                case "vi" -> "Thỉnh thoảng điểm danh";
                case "ko" -> "가끔 출석 부름";
                default -> "偶尔点名";
            };
            case 3 -> switch (lang) {
                case "en" -> "Checks attendance every class";
                case "vi" -> "Điểm danh mỗi buổi học";
                case "ko" -> "매 시간 출석 체크";
                default -> "每节课点名";
            };
            case 4 -> switch (lang) {
                case "en" -> "Random attendance checks";
                case "vi" -> "Điểm danh đột xuất";
                case "ko" -> "무작위 불시 출석";
                default -> "随机点名";
            };
            default -> noInfo(lang);
        };
    }

    private String toHomeworkText(Integer level, String lang) {
        if (level == null) return noInfo(lang);

        return switch (level) {
            case 1 -> switch (lang) {
                case "en" -> "No assignments";
                case "vi" -> "Không có bài tập";
                case "ko" -> "과제 없음";
                default -> "没有作业";
            };
            case 2 -> switch (lang) {
                case "en" -> "Few assignments";
                case "vi" -> "Ít bài tập";
                case "ko" -> "소량 과제만 있음";
                default -> "作业较少";
            };
            case 3 -> switch (lang) {
                case "en" -> "Weekly assignments";
                case "vi" -> "Có bài tập hàng tuần";
                case "ko" -> "매주 과제 나옴";
                default -> "每周有作业";
            };
            case 4 -> switch (lang) {
                case "en" -> "Frequent presentations/reports";
                case "vi" -> "Thường có thuyết trình/báo cáo";
                case "ko" -> "발표/레포트 잦음";
                default -> "经常有发表/报告";
            };
            default -> noInfo(lang);
        };
    }

    private String toExamText(Integer level, String lang) {
        if (level == null) return noInfo(lang);

        return switch (level) {
            case 1 -> switch (lang) {
                case "en" -> "Generous grading";
                case "vi" -> "Chấm điểm khá dễ";
                case "ko" -> "학점 관대함";
                default -> "给分宽松";
            };
            case 2 -> switch (lang) {
                case "en" -> "Average difficulty";
                case "vi" -> "Độ khó trung bình";
                case "ko" -> "보통 난이도";
                default -> "难度普通";
            };
            case 3 -> switch (lang) {
                case "en" -> "Difficult exams";
                case "vi" -> "Bài thi khó";
                case "ko" -> "시험 어려움";
                default -> "考试较难";
            };
            case 4 -> switch (lang) {
                case "en" -> "High fail rate";
                case "vi" -> "Tỷ lệ rớt cao";
                case "ko" -> "낙제율 높음";
                default -> "挂科率高";
            };
            default -> noInfo(lang);
        };
    }

    private String toRiskText(Integer score, String lang) {
        if (score == null) return noInfo(lang);

        return switch (score) {
            case 1 -> switch (lang) {
                case "en" -> "Very low risk";
                case "vi" -> "Rủi ro rất thấp";
                case "ko" -> "위험 매우 낮음";
                default -> "风险非常低";
            };
            case 2 -> switch (lang) {
                case "en" -> "Low risk";
                case "vi" -> "Rủi ro thấp";
                case "ko" -> "위험 낮음";
                default -> "风险低";
            };
            case 3 -> switch (lang) {
                case "en" -> "Medium risk";
                case "vi" -> "Rủi ro trung bình";
                case "ko" -> "보통 위험";
                default -> "风险一般";
            };
            case 4 -> switch (lang) {
                case "en" -> "High risk";
                case "vi" -> "Rủi ro cao";
                case "ko" -> "위험 높음";
                default -> "风险高";
            };
            case 5 -> switch (lang) {
                case "en" -> "Very high risk";
                case "vi" -> "Rủi ro rất cao";
                case "ko" -> "위험 매우 높음";
                default -> "风险非常高";
            };
            default -> noInfo(lang);
        };
    }

    private String noInfo(String lang) {
        return switch (lang) {
            case "en" -> "No information";
            case "vi" -> "Không có thông tin";
            case "ko" -> "정보 없음";
            default -> "无信息";
        };
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

            p.setRollCallLevel(toRollCallText(teacher.getPortrait().getRollCallLevel(), "ko"));
            p.setHomeworkLevel(toHomeworkText(teacher.getPortrait().getHomeworkLevel(), "ko"));
            p.setExamLevel(toExamText(teacher.getPortrait().getExamLevel(), "ko"));
            p.setAttendanceRatio(teacher.getPortrait().getAttendanceRatio());
            p.setStyleTag(teacher.getPortrait().getStyleTag());
            p.setRiskScore(toRiskText(teacher.getPortrait().getRiskScore(), "ko"));

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