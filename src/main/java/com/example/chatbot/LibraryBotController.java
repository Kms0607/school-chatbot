package com.example.chatbot;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LibraryBotController {

    private final Map<String, UserState> userStates = new HashMap<>();

    private static final String KR_SERVICE = "【2026년 제1학기】\n" +
            "[영진전문대학교 도서관]\n" +
            "운영기간: 2026.03.02 ~ 2026.06.24\n" +
            "정보검색실(1층): 주중 8:30~21:00 / 토요일 8:30~17:30 (일요일 휴실)\n" +
            "열람실(2층): 매일 6:00~23:00\n" +
            "자료실(3층): 주중 9:00~21:00 / 토요일 8:30~17:30 (일요일 휴실)\n" +
            "공휴일은 운영하지 않으며, 시험 기간 동안 열람실은 24시간 운영됩니다.";

    private static final String KR_BOOK_LIST = "1위. 나루토 1-72 / 마사시 키시모토\n" +
            "2위. 주술회전 0-30 / 게게 아쿠타미\n" +
            "3위. 배가본드 / 다케히코 이노우에\n" +
            "4위. 향기로운 꽃 / 한유진\n" +
            "5위. 나의 히어로 아카데미아 / 코헤이 호리코시";

    private static final String CN_SERVICE = "【2026年第一学期】\n" +
            "[永进专门大学图书馆]\n" +
            "运营时间：2026.03.02 ~ 2026.06.24\n" +
            "信息检索室(1楼)：工作日 8:30~21:00 / 周六 8:30~17:30（周日闭馆）\n" +
            "阅览室(2楼)：每日 6:00~23:00\n" +
            "资料室(3楼)：工作日 9:00~21:00 / 周六 8:30~17:30（周日闭馆）\n" +
            "公休日不开放，考试期间阅览室24小时开放。";

    private static final String CN_BOOK_LIST = "1名. 火影忍者 1-72 / 岸本齐史\n" +
            "2名. 咒术回战 0-30 / 芥见下下\n" +
            "3名. 浪客行 / 井上雄彦\n" +
            "4名. 散发香气的花朵 / 韩宥真\n" +
            "5名. 我的英雄学院 / 堀越耕平";

    private static final String EN_SERVICE = "【2026 1st Semester】\n" +
            "[Yeungjin University Library]\n" +
            "Operating Period: 2026.03.02 ~ 2026.06.24\n" +
            "Information Search Room(1F): Weekdays 8:30~21:00 / Sat 8:30~17:30 (Closed Sun)\n" +
            "Reading Room(2F): Daily 6:00~23:00\n" +
            "Stack Room(3F): Weekdays 9:00~21:00 / Sat 8:30~17:30 (Closed Sun)\n" +
            "Closed on public holidays. 24h reading room during exam period.";

    private static final String EN_BOOK_LIST = "No.1 Naruto 1-72 / Masashi Kishimoto\n" +
            "No.2 Jujutsu Kaisen 0-30 / Gege Akutami\n" +
            "No.3 Vagabond / Takehiko Inoue\n" +
            "No.4 The Fragrant Flower / Han Yujin\n" +
            "No.5 My Hero Academia / Kohei Horikoshi";

    private static final String VI_SERVICE = "【Học kỳ 1 năm 2026】\n" +
            "[Thư viện Đại học Yeungjin]\n" +
            "Thời gian: 2026.03.02 ~ 2026.06.24\n" +
            "Phòng tra cứu(T1): Ngày thường 8:30~21:00 / Thứ 7 8:30~17:30 (Đóng CN)\n" +
            "Phòng đọc(T2): Hàng ngày 6:00~23:00\n" +
            "Phòng tài liệu(T3): Ngày thường 9:00~21:00 / Thứ 7 8:30~17:30 (Đóng CN)\n" +
            "Đóng cửa ngày lễ. Phòng đọc mở 24h khi thi.";

    private static final String VI_BOOK_LIST = "Hạng 1 Naruto 1-72 / Masashi Kishimoto\n" +
            "Hạng 2 Jujutsu Kaisen 0-30 / Gege Akutami\n" +
            "Hạng 3 Vagabond / Takehiko Inoue\n" +
            "Hạng 4 Hoa thơm / Han Yujin\n" +
            "Hạng 5 My Hero Academia / Kohei Horikoshi";

    @PostMapping("/library/bot")
    public Map<String, Object> handleMessage(@RequestBody Map<String, Object> request) {

        Map<String, Object> userRequest =
                (Map<String, Object>) request.get("userRequest");

        String content = String.valueOf(userRequest.get("utterance")).trim();

        Map<String, Object> user =
                (Map<String, Object>) userRequest.get("user");

        String userId = String.valueOf(user.get("id"));

        UserState state = userStates.getOrDefault(userId, new UserState());

        String selectedLang = detectLang(content);

        if (selectedLang != null) {
            state.lang = selectedLang;
            state.step = null;
            userStates.put(userId, state);

            return buildResponse(getLangSetMessage(state.lang));
        }

        if (isLibraryStart(content)) {
            state.step = "WAIT_MENU";
            userStates.put(userId, state);

            return buildResponse(getMenu(state.lang));
        }

        if ("WAIT_MENU".equals(state.step) || isLibraryMenu(content)) {

            state.step = null;
            userStates.put(userId, state);

            if (isServiceMenu(content)) {
                return buildResponse(getService(state.lang));
            }

            if (isBookMenu(content)) {
                return buildResponse(getBestseller(state.lang));
            }

            return buildResponse(getError(state.lang));
        }

        return buildResponse(getHelp(state.lang));
    }

    private boolean isLibraryStart(String content) {
        String lower = content.toLowerCase();

        return content.contains("도서관")
                || lower.contains("library")
                || content.contains("图书馆")
                || lower.contains("thư viện");
    }

    private boolean isLibraryMenu(String content) {
        return isServiceMenu(content) || isBookMenu(content);
    }

    private boolean isServiceMenu(String content) {
        String lower = content.toLowerCase();

        return "1".equals(content)
                || content.contains("전체 서비스")
                || content.contains("도서관 전체 서비스")
                || content.contains("全部服务")
                || lower.contains("all services")
                || lower.contains("all service")
                || lower.contains("tất cả dịch vụ");
    }

    private boolean isBookMenu(String content) {
        String lower = content.toLowerCase();

        return "2".equals(content)
                || content.contains("인기 도서")
                || content.contains("도서관 인기 도서 순위")
                || content.contains("畅销榜单")
                || lower.contains("bestseller")
                || lower.contains("book list")
                || lower.contains("danh sách bán chạy");
    }

    private String detectLang(String content) {
        String lower = content.toLowerCase();

        if (content.contains("한국")
                || content.equalsIgnoreCase("ko")
                || content.equalsIgnoreCase("kr")) {
            return "kr";
        }

        if (content.contains("중국")
                || content.contains("中文")
                || content.equalsIgnoreCase("zh")
                || content.equalsIgnoreCase("cn")) {
            return "cn";
        }

        if (content.contains("영어")
                || content.equalsIgnoreCase("en")
                || lower.contains("english")) {
            return "en";
        }

        if (content.contains("베트남")
                || content.equalsIgnoreCase("vi")
                || lower.contains("tiếng việt")) {
            return "vi";
        }

        return null;
    }

    private String getLangSetMessage(String lang) {
        switch (lang) {
            case "cn":
                return "语言已设置为中文。\n请问需要什么帮助？";
            case "en":
                return "Language has been set to English.\nHow can I help you?";
            case "vi":
                return "Ngôn ngữ đã được đặt thành tiếng Việt.\nTôi có thể giúp gì cho bạn?";
            default:
                return "한국어로 설정되었습니다.\n무엇을 도와드릴까요?";
        }
    }

    private String getHelp(String lang) {
        switch (lang) {
            case "cn":
                return "请问需要什么帮助？\n可以输入【图书馆】查询图书馆信息。";
            case "en":
                return "How can I help you?\nEnter [library] to check library information.";
            case "vi":
                return "Tôi có thể giúp gì cho bạn?\nNhập [thư viện] để xem thông tin thư viện.";
            default:
                return "무엇을 도와드릴까요?\n도서관 정보를 보려면 '도서관'을 입력하세요.";
        }
    }

    private String getMenu(String lang) {
        switch (lang) {
            case "cn":
                return "菜单选择：\n1. 全部服务\n2. 畅销榜单";
            case "en":
                return "Menu:\n1. All Services\n2. Bestseller List";
            case "vi":
                return "Menu:\n1. Tất cả dịch vụ\n2. Danh sách bán chạy";
            default:
                return "메뉴를 선택하세요.\n1. 전체 서비스\n2. 인기 도서 순위";
        }
    }

    private String getError(String lang) {
        switch (lang) {
            case "cn":
                return "输入错误。请重新输入。";
            case "en":
                return "Input error. Please try again.";
            case "vi":
                return "Lỗi đầu vào. Vui lòng thử lại.";
            default:
                return "입력 오류입니다. 다시 입력해 주세요.";
        }
    }

    private String getService(String lang) {
        switch (lang) {
            case "cn":
                return CN_SERVICE;
            case "en":
                return EN_SERVICE;
            case "vi":
                return VI_SERVICE;
            default:
                return KR_SERVICE;
        }
    }

    private String getBestseller(String lang) {
        switch (lang) {
            case "cn":
                return CN_BOOK_LIST;
            case "en":
                return EN_BOOK_LIST;
            case "vi":
                return VI_BOOK_LIST;
            default:
                return KR_BOOK_LIST;
        }
    }

    private Map<String, Object> buildResponse(String text) {
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
        String lang = "kr";
        String step;
    }

    @GetMapping("/library/notice/kr")
    public String krNotice() {
        return KR_SERVICE;
    }

    @GetMapping("/library/book/kr")
    public String krBook() {
        return KR_BOOK_LIST;
    }

    @GetMapping("/library/all/kr")
    public String krAll() {
        return KR_SERVICE + "\n\n===== 인기 도서 순위 =====\n" + KR_BOOK_LIST;
    }

    @GetMapping("/library/notice/cn")
    public String cnNotice() {
        return CN_SERVICE;
    }

    @GetMapping("/library/book/cn")
    public String cnBook() {
        return CN_BOOK_LIST;
    }

    @GetMapping("/library/all/cn")
    public String cnAll() {
        return CN_SERVICE + "\n\n===== 畅销榜单 =====\n" + CN_BOOK_LIST;
    }

    @GetMapping("/library/notice/en")
    public String enNotice() {
        return EN_SERVICE;
    }

    @GetMapping("/library/book/en")
    public String enBook() {
        return EN_BOOK_LIST;
    }

    @GetMapping("/library/all/en")
    public String enAll() {
        return EN_SERVICE + "\n\n===== Bestseller List =====\n" + EN_BOOK_LIST;
    }

    @GetMapping("/library/notice/vi")
    public String viNotice() {
        return VI_SERVICE;
    }

    @GetMapping("/library/book/vi")
    public String viBook() {
        return VI_BOOK_LIST;
    }

    @GetMapping("/library/all/vi")
    public String viAll() {
        return VI_SERVICE + "\n\n===== Danh sách bán chạy =====\n" + VI_BOOK_LIST;
    }
}