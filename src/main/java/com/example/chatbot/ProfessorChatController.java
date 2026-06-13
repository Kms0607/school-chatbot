package com.example.chatbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ProfessorChatController {


    private static final String PROF_WAIT_GYEYEOL = "PROF_WAIT_GYEYEOL";
    private static final String PROF_WAIT_NAME = "PROF_WAIT_NAME";

    private final ProfessorService professorService;
    private final UserSession session;

    public ProfessorChatController(ProfessorService professorService, UserSession session) {
        this.professorService = professorService;
        this.session = session;
    }

    @PostMapping("/chat/professor")
    public ResponseEntity<Map<String, Object>> getProfChatInfo(@RequestBody Map<String, Object> req) {

        Map<String, Object> userRequest = (Map<String, Object>) req.get("userRequest");

        String userSessionId = "defaultUser";
        String userInput = "";

        if (userRequest != null) {
            String rawText = (String) userRequest.getOrDefault("utterance", "");
            userInput = rawText.trim();

            Object userObj = userRequest.get("user");
            if (userObj instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userObj;
                Object idObj = userMap.get("id");
                if (idObj != null) {
                    userSessionId = String.valueOf(idObj);
                }
            }
        }

        String replyText;
        String step = session.getStep(userSessionId);

        // 0. 교수 조회 모드 탈출
        if (isExitCommand(userInput)) {
            session.setStep(userSessionId, null);

            replyText = getMessageByLang(
                    userSessionId,
                    """
                    메인 메뉴로 돌아왔습니다.
                    
                    이용할 메뉴를 선택하거나 입력해주세요.
                    1. 학사일정
                    2. 시간표
                    3. 교수 정보
                    4. 도서관 안내
                    """,
                    """
                    已返回主菜单。
                    
                    请选择或输入要使用的菜单。
                    1. 学事日程
                    2. 课程表
                    3. 教授信息
                    4. 图书馆指南
                    """,
                    """
                    Returned to the main menu.
                    
                    Please select or enter a menu.
                    1. Academic Schedule
                    2. Timetable
                    3. Professor Information
                    4. Library Guide
                    """
            );

            return ResponseEntity.ok(kakaoResponse(replyText));
        }

        // 1. 교수 정보 메뉴 진입 → 계열 선택 카드 버튼 출력
        if (isProfessorMenu(userInput)) {
            session.setStep(userSessionId, PROF_WAIT_GYEYEOL);
            return ResponseEntity.ok(gyeyeolCardResponse(userSessionId));
        }

        // 2. 계열 선택 단계
        if (PROF_WAIT_GYEYEOL.equals(step)) {
            if (!isComputerGyeyeol(userInput)) {
                return ResponseEntity.ok(gyeyeolCardResponse(userSessionId));
            }

            session.setStep(userSessionId, PROF_WAIT_NAME);

            List<ProfessorVO> professors = professorService.listByGyeyeolName("컴퓨터정보계열");

            if (professors.isEmpty()) {
                replyText = getMessageByLang(
                        userSessionId,
                        "등록된 교수 목록이 없습니다.",
                        "没有已登记的教授列表。",
                        "There is no registered professor list."
                );
            } else {
                String names = professors.stream()
                        .map(ProfessorVO::getNameKr)
                        .distinct()
                        .collect(Collectors.joining("\n"));

                replyText = getMessageByLang(
                        userSessionId,
                        "컴퓨터정보계열 교수 목록입니다.\n\n"
                                + names
                                + "\n\n교수님 상세정보를 보시려면 교수명을 입력해주세요."
                                + "\n메인 메뉴로 돌아가려면 '메뉴'를 입력해주세요.",
                        "计算机信息系教授列表如下。\n\n"
                                + names
                                + "\n\n如需查看教授详细信息，请输入教授姓名。"
                                + "\n如需返回主菜单，请输入“菜单”。",
                        "Here is the professor list for the Computer Information Division.\n\n"
                                + names
                                + "\n\nTo view detailed professor information, please enter the professor's name."
                                + "\nTo return to the main menu, enter 'menu'."
                );
            }

            return ResponseEntity.ok(kakaoResponse(replyText));
        }

        // 3. 교수 이름 입력 단계
        String searchName = userInput
                .replace("교수님", "")
                .replace("교수", "")
                .trim();

        if (searchName.isBlank()) {
            replyText = getMessageByLang(
                    userSessionId,
                    "교수님 성함을 입력해주세요.\n예시: 김경태 교수님",
                    "请输入教授姓名。\n例：金敬泰 教授",
                    "Please enter the professor's name.\nExample: Professor Kim Kyung-tae"
            );
        } else {
            Optional<ProfessorVO> data = professorService.getByName(searchName);

            if (data.isEmpty()) {
                replyText = getMessageByLang(
                        userSessionId,
                        searchName + " 교수님에 대한 정보를 찾을 수 없습니다."
                                + "\n다른 교수명을 입력하거나, 메인 메뉴로 돌아가려면 '메뉴'를 입력해주세요.",
                        "未找到 " + searchName + " 教授的信息。"
                                + "\n请输入其他教授姓名，或输入“菜单”返回主菜单。",
                        "Could not find information for Professor " + searchName + "."
                                + "\nPlease enter another professor's name, or enter 'menu' to return to the main menu."
                );

                session.setStep(userSessionId, PROF_WAIT_NAME);
            } else {
                ProfessorVO p = data.get();

                String lang = session.getLang(userSessionId);

                replyText = switch (lang) {
                    case "zh" -> buildCnText(p)
                            + "\n\n如需查看其他教授详细信息，请输入教授姓名。"
                            + "\n如需返回主菜单，请输入“菜单”。";
                    case "en" -> buildEnText(p)
                            + "\n\nTo view another professor's details, please enter the professor's name."
                            + "\nTo return to the main menu, enter 'menu'.";
                    default -> buildKrText(p)
                            + "\n\n다른 교수님의 상세정보를 보시려면 교수명을 입력해주세요."
                            + "\n메인 메뉴로 돌아가려면 '메뉴'를 입력해주세요.";
                };

                session.setStep(userSessionId, PROF_WAIT_NAME);
            }
        }

        return ResponseEntity.ok(kakaoResponse(replyText));
    }

    private boolean isProfessorMenu(String input) {
        return input.equals("교수 정보")
                || input.equals("교수정보")
                || input.equals("교수 조회")
                || input.equals("교수조회")
                || input.equals("교수 검색")
                || input.equals("교수검색")
                || input.equals("교수님 정보")
                || input.equals("교수님정보")
                || input.equals("Professor Info")
                || input.equals("Professor")
                || input.equals("教授信息")
                || input.equals("教授查询");
    }

    private boolean isComputerGyeyeol(String input) {
        return input.equals("컴퓨터정보계열")
                || input.equals("计算机信息系")
                || input.equals("Computer Information Division");
    }

    private boolean isExitCommand(String input) {
        return input.equals("메뉴")
                || input.equals("처음으로")
                || input.equals("뒤로가기")
                || input.equals("종료")
                || input.equalsIgnoreCase("menu")
                || input.equalsIgnoreCase("back")
                || input.equalsIgnoreCase("exit")
                || input.equals("菜单")
                || input.equals("返回")
                || input.equals("退出");
    }

    private String getMessageByLang(String userSessionId, String kr, String cn, String en) {
        String lang = session.getLang(userSessionId);

        return switch (lang) {
            case "zh" -> cn;
            case "en" -> en;
            default -> kr;
        };
    }

    private String buildKrText(ProfessorVO p) {
        return String.format("""
            %s 교수님 상세 정보
            학과: %s
            계열: %s
            사무실: %s
            연락처: %s
            이메일: %s
            직급: %s
            """,
                p.getNameKr(),
                p.getHakgwaNameKr(),
                p.getGyeyeolNameKr(),
                p.getOfficeRoom() == null ? "없음" : p.getOfficeRoom(),
                p.getPhone() == null ? "없음" : p.getPhone(),
                p.getEmail() == null ? "없음" : p.getEmail(),
                p.getPositionTitle() == null ? "없음" : p.getPositionTitle()
        );
    }

    private String buildCnText(ProfessorVO p) {
        String deptCn = KoreanTranslateUtil1.transHakgwaToCn(p.getHakgwaNameKr());
        String posCn = KoreanTranslateUtil1.transPositionToCn(p.getPositionTitle());
        String gyeyeolCn = KoreanTranslateUtil1.transGyeyeolToCn(p.getGyeyeolNameKr());

        return String.format("""
            %s 教授 详细信息
            学科：%s
            系列：%s
            办公室：%s
            联系电话：%s
            邮箱：%s
            职位：%s
            """,
                p.getNameKr(),
                deptCn,
                gyeyeolCn,
                p.getOfficeRoom() == null ? "无" : p.getOfficeRoom(),
                p.getPhone() == null ? "无" : p.getPhone(),
                p.getEmail() == null ? "无" : p.getEmail(),
                posCn
        );
    }

    private String buildEnText(ProfessorVO p) {
        String deptEn = KoreanTranslateUtil1.transHakgwaToEn(p.getHakgwaNameKr());
        String posEn = KoreanTranslateUtil1.transPositionToEn(p.getPositionTitle());
        String gyeyeolEn = KoreanTranslateUtil1.transGyeyeolToEn(p.getGyeyeolNameKr());

        return String.format("""
            Professor %s Info
            Department: %s
            Division: %s
            Office Room: %s
            Phone: %s
            Email: %s
            Position: %s
            """,
                p.getNameKr(),
                deptEn,
                gyeyeolEn,
                p.getOfficeRoom() == null ? "N/A" : p.getOfficeRoom(),
                p.getPhone() == null ? "N/A" : p.getPhone(),
                p.getEmail() == null ? "N/A" : p.getEmail(),
                posEn
        );
    }

    private Map<String, Object> gyeyeolCardResponse(String userSessionId) {
        String title = getMessageByLang(
                userSessionId,
                "교수 정보 조회",
                "教授信息查询",
                "Professor Information"
        );

        String description = getMessageByLang(
                userSessionId,
                "계열을 선택해주세요.",
                "请选择系列。",
                "Please select a division."
        );

        String buttonLabel = getMessageByLang(
                userSessionId,
                "컴퓨터정보계열",
                "计算机信息系",
                "Computer Information Division"
        );

        Map<String, Object> button = new HashMap<>();
        button.put("action", "message");
        button.put("label", buttonLabel);
        button.put("messageText", buttonLabel);

        Map<String, Object> card = new HashMap<>();
        card.put("title", title);
        card.put("description", description);
        card.put("buttons", List.of(button));

        Map<String, Object> output = new HashMap<>();
        output.put("basicCard", card);

        Map<String, Object> template = new HashMap<>();
        template.put("outputs", List.of(output));

        Map<String, Object> res = new HashMap<>();
        res.put("version", "2.0");
        res.put("template", template);

        return res;
    }

    private Map<String, Object> kakaoResponse(String text) {
        Map<String, Object> simpleText = new HashMap<>();
        simpleText.put("text", text);

        Map<String, Object> output = new HashMap<>();
        output.put("simpleText", simpleText);

        Map<String, Object> template = new HashMap<>();
        template.put("outputs", List.of(output));

        Map<String, Object> res = new HashMap<>();
        res.put("version", "2.0");
        res.put("template", template);

        return res;
    }


}
