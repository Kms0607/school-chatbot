package com.example.chatbot;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class KoreanTranslateUtil {

    private static final Map<String, Map<String, String>> weekMap = new HashMap<>();
    private static final Map<String, Map<String, String>> subjectMap = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> professorMap = new HashMap<>();

    static {
        weekMap.put("월", Map.of("zh", "周一", "en", "Monday", "vi", "Thứ Hai"));
        weekMap.put("화", Map.of("zh", "周二", "en", "Tuesday", "vi", "Thứ Ba"));
        weekMap.put("수", Map.of("zh", "周三", "en", "Wednesday", "vi", "Thứ Tư"));
        weekMap.put("목", Map.of("zh", "周四", "en", "Thursday", "vi", "Thứ Năm"));
        weekMap.put("금", Map.of("zh", "周五", "en", "Friday", "vi", "Thứ Sáu"));
        weekMap.put("토", Map.of("zh", "周六", "en", "Saturday", "vi", "Thứ Bảy"));

        subjectMap.put("컴퓨팅사고와 SW코딩", Map.of("zh", "计算思维与SW编码", "en", "Computational Thinking and SW Coding", "vi", "Tư duy máy tính và mã hóa SW"));
        subjectMap.put("컴퓨팅사고와SW코딩", Map.of("zh", "计算思维与SW编码", "en", "Computational Thinking and SW Coding", "vi", "Tư duy máy tính và mã hóa SW"));
        subjectMap.put("프레젠테이션기술", Map.of("zh", "演示技术", "en", "Presentation Skills", "vi", "Kỹ năng thuyết trình"));
        subjectMap.put("컴퓨터그래픽", Map.of("zh", "计算机图形学", "en", "Computer Graphics", "vi", "Đồ họa máy tính"));
        subjectMap.put("실무영어", Map.of("zh", "实用英语", "en", "Practical English", "vi", "Tiếng Anh thực hành"));
        subjectMap.put("컴퓨터개론", Map.of("zh", "计算机概论", "en", "Introduction to Computer Science", "vi", "Nhập môn máy tính"));
        subjectMap.put("대인관계능력", Map.of("zh", "人际交往能力", "en", "Interpersonal Skills", "vi", "Kỹ năng giao tiếp"));
        subjectMap.put("기초일본어회화", Map.of("zh", "基础日语会话", "en", "Basic Japanese Conversation", "vi", "Hội thoại tiếng Nhật cơ bản"));

        subjectMap.put("정보처리산업기사", Map.of("zh", "信息处理产业工程师", "en", "Industrial Engineer Information Processing", "vi", "Kỹ sư công nghiệp xử lý thông tin"));
        subjectMap.put("객체지향프로그래밍", Map.of("zh", "面向对象编程", "en", "Object-Oriented Programming", "vi", "Lập trình hướng đối tượng"));
        subjectMap.put("객체지향 프로그래밍", Map.of("zh", "面向对象编程", "en", "Object-Oriented Programming", "vi", "Lập trình hướng đối tượng"));
        subjectMap.put("AI데이터분석", Map.of("zh", "AI数据分析", "en", "AI Data Analysis", "vi", "Phân tích dữ liệu AI"));
        subjectMap.put("프론트엔드프로그래밍", Map.of("zh", "前端编程", "en", "Frontend Programming", "vi", "Lập trình Frontend"));
        subjectMap.put("프론트엔드 프로그래밍", Map.of("zh", "前端编程", "en", "Frontend Programming", "vi", "Lập trình Frontend"));
        subjectMap.put("클라우드시스템", Map.of("zh", "云系统", "en", "Cloud System", "vi", "Hệ thống đám mây"));
        subjectMap.put("클라우드 시스템", Map.of("zh", "云系统", "en", "Cloud System", "vi", "Hệ thống đám mây"));
        subjectMap.put("DB언어", Map.of("zh", "数据库语言", "en", "DB Language", "vi", "Ngôn ngữ DB"));
        subjectMap.put("직업윤리", Map.of("zh", "职业伦理", "en", "Professional Ethics", "vi", "Đạo đức nghề nghiệp"));
        subjectMap.put("리눅스시스템", Map.of("zh", "Linux系统", "en", "Linux System", "vi", "Hệ thống Linux"));
        subjectMap.put("리눅스 시스템", Map.of("zh", "Linux系统", "en", "Linux System", "vi", "Hệ thống Linux"));
        subjectMap.put("자료구조와알고리즘", Map.of("zh", "数据结构与算法", "en", "Data Structures and Algorithms", "vi", "Cấu trúc dữ liệu và thuật toán"));
        subjectMap.put("자료구조와 알고리즘", Map.of("zh", "数据结构与算法", "en", "Data Structures and Algorithms", "vi", "Cấu trúc dữ liệu và thuật toán"));
        subjectMap.put("서버시스템구축", Map.of("zh", "服务器系统构建", "en", "Server System Construction", "vi", "Xây dựng hệ thống máy chủ"));
        subjectMap.put("서버 시스템 구축", Map.of("zh", "服务器系统构建", "en", "Server System Construction", "vi", "Xây dựng hệ thống máy chủ"));
        subjectMap.put("캡스톤디자인", Map.of("zh", "毕业项目设计", "en", "Capstone Design", "vi", "Thiết kế Capstone"));
        subjectMap.put("캡스톤 디자인", Map.of("zh", "毕业项目设计", "en", "Capstone Design", "vi", "Thiết kế Capstone"));
        subjectMap.put("머신러닝", Map.of("zh", "机器学习", "en", "Machine Learning", "vi", "Học máy"));

        subjectMap.put("컴퓨터", Map.of("zh", "计算机", "en", "Computer", "vi", "Máy tính"));
        subjectMap.put("그래픽", Map.of("zh", "图形", "en", "Graphics", "vi", "Đồ họa"));
        subjectMap.put("컴퓨팅", Map.of("zh", "计算", "en", "Computing", "vi", "Tính toán"));
        subjectMap.put("사고", Map.of("zh", "思维", "en", "Thinking", "vi", "Tư duy"));
        subjectMap.put("코딩", Map.of("zh", "编码", "en", "Coding", "vi", "Mã hóa"));
        subjectMap.put("영어", Map.of("zh", "英语", "en", "English", "vi", "Tiếng Anh"));
        subjectMap.put("일본어", Map.of("zh", "日语", "en", "Japanese", "vi", "Tiếng Nhật"));
        subjectMap.put("회화", Map.of("zh", "会话", "en", "Conversation", "vi", "Hội thoại"));
        subjectMap.put("AI", Map.of("zh", "人工智能", "en", "AI", "vi", "AI"));
        subjectMap.put("소프트웨어", Map.of("zh", "软件", "en", "Software", "vi", "Phần mềm"));
        subjectMap.put("프로그래밍", Map.of("zh", "编程", "en", "Programming", "vi", "Lập trình"));
        subjectMap.put("데이터", Map.of("zh", "数据", "en", "Data", "vi", "Dữ liệu"));
        subjectMap.put("백엔드", Map.of("zh", "后端", "en", "Backend", "vi", "Backend"));
        subjectMap.put("개론", Map.of("zh", "概论", "en", "Introduction", "vi", "Giới thiệu"));
        subjectMap.put("기술", Map.of("zh", "技术", "en", "Skills", "vi", "Kỹ thuật"));
        subjectMap.put("관리", Map.of("zh", "管理", "en", "Management", "vi", "Quản lý"));
        subjectMap.put("시스템", Map.of("zh", "系统", "en", "System", "vi", "Hệ thống"));
        subjectMap.put("보안", Map.of("zh", "安全", "en", "Security", "vi", "Bảo mật"));
        subjectMap.put("게임", Map.of("zh", "游戏", "en", "Game", "vi", "Trò chơi"));
        subjectMap.put("네트워크", Map.of("zh", "网络", "en", "Network", "vi", "Mạng"));
        subjectMap.put("서버", Map.of("zh", "服务器", "en", "Server", "vi", "Máy chủ"));
        subjectMap.put("프로젝트", Map.of("zh", "项目", "en", "Project", "vi", "Dự án"));
        subjectMap.put("기초", Map.of("zh", "基础", "en", "Basics", "vi", "Cơ bản"));
        subjectMap.put("DB", Map.of("zh", "数据库", "en", "Database", "vi", "Cơ sở dữ liệu"));

        professorMap.put("교수", Map.of("zh", "教授", "en", "Professor", "vi", "Giáo sư"));
    }

    private String checkLang(String lang) {
        if (lang == null) {
            return "ko";
        }

        if ("ko".equals(lang)
                || "en".equals(lang)
                || "vi".equals(lang)
                || "zh".equals(lang)) {
            return lang;
        }

        return "ko";
    }

    public String translateWeek(String week, String lang) {
        lang = checkLang(lang);

        if ("ko".equals(lang)) {
            return week;
        }

        return weekMap
                .getOrDefault(week, new HashMap<>())
                .getOrDefault(lang, week);
    }

    public String translateSubject(String subject, String lang) {
        if (subject == null || subject.isEmpty()) {
            return subject;
        }

        lang = checkLang(lang);

        if ("ko".equals(lang)) {
            return subject;
        }

        String result = subject;
        String compareText = subject.replace(" ", "");

        for (Map.Entry<String, Map<String, String>> entry : subjectMap.entrySet()) {
            String key = entry.getKey();
            String compareKey = key.replace(" ", "");

            if (compareText.contains(compareKey)) {
                result = result.replace(key, entry.getValue().get(lang));
                result = result.replace(key.replace(" ", ""), entry.getValue().get(lang));
            }
        }

        return translateSuffix(result, lang);
    }

    private String translateSuffix(String text, String lang) {
        if ("ko".equals(lang)) {
            return text;
        }

        if ("zh".equals(lang)) {
            return text.replace("분반", "班")
                    .replace("(1학년)", "(1年级)")
                    .replace("(2학년)", "(2年级)");
        }

        if ("en".equals(lang)) {
            return text.replace("분반", "Class")
                    .replace("(1학년)", "(Year 1)")
                    .replace("(2학년)", "(Year 2)");
        }

        if ("vi".equals(lang)) {
            return text.replace("분반", "Lớp")
                    .replace("(1학년)", "(Lớp 1)")
                    .replace("(2학년)", "(Lớp 2)");
        }

        return text;
    }

    public String translateProfessor(String professor, String lang) {
        if (professor == null || professor.isEmpty()) {
            return professor;
        }

        lang = checkLang(lang);

        if ("ko".equals(lang)) {
            return professor;
        }

        String result = professor;

        for (Map.Entry<String, Map<String, String>> entry : professorMap.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue().get(lang));
            }
        }

        return result;
    }
}