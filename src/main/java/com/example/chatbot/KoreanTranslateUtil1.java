package com.example.chatbot;

public class KoreanTranslateUtil1 {


    // 학과 한글 → 중국어
    public static String transHakgwaToCn(String hakgwaKr) {
        return switch (hakgwaKr) {
            case "AI소프트웨어" -> "AI软件工程";
            case "IT클라우드보안" -> "IT云安全";
            case "AI글로벌IT" -> "AI全球IT";
            case "AI게임메타버스" -> "AI游戏元宇宙";
            case "게임메타버스" -> "游戏元宇宙";
            case "etc" -> "其他";
            default -> hakgwaKr;
        };
    }

    // 학과 한글 → 영어
    public static String transHakgwaToEn(String hakgwaKr) {
        return switch (hakgwaKr) {
            case "AI소프트웨어" -> "AI Software Engineering";
            case "IT클라우드보안" -> "IT Cloud Security";
            case "AI글로벌IT" -> "AI Global IT";
            case "AI게임메타버스" -> "AI Game Metaverse";
            case "게임메타버스" -> "Game Metaverse";
            case "etc" -> "Others";
            default -> hakgwaKr;
        };
    }

    // 직급 한글 → 중국어
    public static String transPositionToCn(String posKr) {
        if (posKr == null) return "无";

        if (posKr.contains("학과장")) return "学科长";
        if (posKr.contains("주임교수") || posKr.contains("전임교수")) return "专任教授";
        if (posKr.contains("취창업위원")) return "创业支援委员";
        if (posKr.contains("전공심화과정운영위원")) return "深化专业课程运营委员";
        if (posKr.contains("평생교육원 원장")) return "终身教育院院长";
        if (posKr.contains("IT지원센터 소장")) return "IT支援中心所长";
        if (posKr.contains("가상공학센터 소장")) return "虚拟工程中心所长";
        if (posKr.contains("컴퓨터정보계열 부장")) return "计算机信息系部长";
        if (posKr.contains("부단장")) return "副院长";
        if (posKr.contains("단장") && posKr.contains("인공지능혁신융합대학")) return "人工智能创新融合大学院长";
        if (posKr.contains("국제교류원 팀장")) return "国际交流院组长";
        if (posKr.contains("기획조정실 실장")) return "企划调整室室长";
        if (posKr.contains("명예교수")) return "名誉教授";
        if (posKr.contains("주문식교육 담당교수")) return "定制化教育负责教授";
        if (posKr.contains("원장")) return "院长";
        if (posKr.contains("소장")) return "所长";
        if (posKr.contains("부장")) return "部长";
        if (posKr.contains("팀장")) return "组长";

        return posKr;
    }

    // 직급 한글 → 영어
    public static String transPositionToEn(String posKr) {
        if (posKr == null) return "N/A";

        if (posKr.contains("학과장")) return "Department Head";
        if (posKr.contains("주임교수") || posKr.contains("전임교수")) return "Full-time Professor";
        if (posKr.contains("취창업위원")) return "Entrepreneurship Support Committee Member";
        if (posKr.contains("전공심화과정운영위원")) return "Major Intensification Program Operations Committee Member";
        if (posKr.contains("평생교육원 원장")) return "Director of Lifelong Education Institute";
        if (posKr.contains("IT지원센터 소장")) return "Director of IT Support Center";
        if (posKr.contains("가상공학센터 소장")) return "Director of Virtual Engineering Center";
        if (posKr.contains("컴퓨터정보계열 부장")) return "Head of Computer Information Division";
        if (posKr.contains("부단장")) return "Vice Dean";
        if (posKr.contains("단장") && posKr.contains("인공지능혁신융합대학")) return "Dean of AI Innovation Convergence College";
        if (posKr.contains("국제교류원 팀장")) return "Team Leader of International Exchange Office";
        if (posKr.contains("기획조정실 실장")) return "Chief of Planning & Coordination Office";
        if (posKr.contains("명예교수")) return "Emeritus Professor";
        if (posKr.contains("주문식교육 담당교수")) return "Professor in Charge of Customized Education";
        if (posKr.contains("원장")) return "Director";
        if (posKr.contains("소장")) return "Center Director";
        if (posKr.contains("부장")) return "Manager";
        if (posKr.contains("팀장")) return "Team Leader";

        return posKr;
    }

    // 계열 한글 → 중국어
    public static String transGyeyeolToCn(String gyeyeolKr) {
        if (gyeyeolKr == null) return "无";

        return switch (gyeyeolKr) {
            case "컴퓨터정보계열" -> "计算机信息系";
            default -> gyeyeolKr;
        };
    }

    // 계열 한글 → 영어
    public static String transGyeyeolToEn(String gyeyeolKr) {
        if (gyeyeolKr == null) return "N/A";

        return switch (gyeyeolKr) {
            case "컴퓨터정보계열" -> "Computer Information Division";
            default -> gyeyeolKr;
        };
    }


}
