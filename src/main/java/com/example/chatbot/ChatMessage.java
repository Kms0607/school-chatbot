package com.example.chatbot;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ChatMessage {

    private final Map<String, Map<String, String>> msgMap = Map.of(
            "ko", Map.of(
                    "lang_tip", "언어를 선택하세요:\n1.한국어 2.중국어 3.영어 4.베트남어",
                    "welcome", "학사일정 조회 서비스입니다.",
                    "year_tip", "2026을 입력해 주세요.",
                    "menu_tip", "메뉴 선택: 1.전체 일정 2.월별 일정",
                    "month_tip", "월을 입력하세요 (1~12)",
                    "reset_notice", "입력이 잘못되었습니다.\n'학사일정'을 입력하여 다시 시작해 주세요."
            ),
            "zh", Map.of(
                    "welcome", "校历查询服务",
                    "year_tip", "请输入 2026",
                    "menu_tip", "请选择：1.查看全部日程 2.按月查询",
                    "month_tip", "请输入月份（1-12）",
                    "reset_notice", "输入错误，已重置。\n请输入「학사일정」重新开始。"
            ),
            "en", Map.of(
                    "welcome", "School Calendar Service",
                    "year_tip", "Please enter 2026",
                    "menu_tip", "Choose: 1.Full schedule 2.Monthly schedule",
                    "month_tip", "Enter month (1-12)",
                    "reset_notice", "Invalid input.\nPlease enter '학사일정' to restart."
            ),
            "vi", Map.of(
                    "welcome", "Dịch vụ tra cứu lịch trường",
                    "year_tip", "Vui lòng nhập 2026",
                    "menu_tip", "Chọn: 1.Lịch toàn bộ 2.Lịch theo tháng",
                    "month_tip", "Nhập tháng (1-12)",
                    "reset_notice", "Đầu vào không hợp lệ.\nNhập '학사일정' để bắt đầu lại."
            )
    );

    // 语言选择阶段固定用韩语提示
    public String getLangTip() {
        return msgMap.get("ko").get("lang_tip");
    }

    // 选完语言后，按用户语言返回对应提示
    public String get(String lang, String key) {
        return msgMap.getOrDefault(lang, msgMap.get("zh")).getOrDefault(key, msgMap.get("zh").get(key));
    }
}