package com.example.chatbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.example.chatbot.KoreanTranslateUtil1;

@RestController
public class ProfessorChatController {

    private final ProfessorService professorService;
    private final Map<String, Integer> userLastProfIdMap = new ConcurrentHashMap<>();

    public ProfessorChatController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping("/chat/professor")
    public ResponseEntity<Map<String, Object>> getProfChatInfo(@RequestBody Map<String, Object> req) {
        Map<String, Object> userRequest = (Map<String, Object>) req.get("userRequest");
        String userSessionId = "defaultUser";
        Object botObj = req.get("bot");
        if (botObj instanceof Map) {
            Map<String, Object> botMap = (Map<String, Object>) botObj;
            Object userIdObj = botMap.get("user");
            if (userIdObj != null) {
                userSessionId = String.valueOf(userIdObj);
            }
        }

        String userInput = "";
        if (userRequest != null) {
            String rawText = (String) userRequest.getOrDefault("utterance", "");
            userInput = rawText.trim();
        }

        String replyText;
        // 这里改为 English
        if ("한국어".equals(userInput) || "中文".equals(userInput) || "English".equals(userInput)) {
            Integer savedProfId = userLastProfIdMap.get(userSessionId);
            if (savedProfId == null) {
                replyText = "먼저 교수님 성함을 검색해 주세요.";
            } else {
                Optional<ProfessorVO> optVo = professorService.getById(savedProfId);
                if (optVo.isEmpty()) {
                    replyText = "교수 정보를 불러올 수 없습니다.";
                } else {
                    ProfessorVO vo = optVo.get();
                    replyText = switch (userInput) {
                        case "한국어" -> buildKrText(vo);
                        case "中文" -> buildCnText(vo);
                        case "English" -> buildEnText(vo);
                        default -> "잘못된 언어 입력";
                    };
                }
            }
        } else {
            String searchName = userInput
                    .replace("교수님", "")
                    .replace("교수", "")
                    .trim();

            if (searchName.isBlank()) {
                replyText = "교수님 성함을 입력해주세요.\n예시: 임덕성 교수님";
            } else {
                Optional<ProfessorVO> data = professorService.getByName(searchName);
                if (data.isEmpty()) {
                    replyText = searchName + " 교수님에 대한 정보를 찾을 수 없습니다.";
                } else {
                    ProfessorVO p = data.get();
                    userLastProfIdMap.put(userSessionId, p.getProfId());

                    if ("컴퓨터정보계열".equals(p.getGyeyeolNameKr())) {
                        // 提示文字同步改为 English
                        replyText = """
                                해당 교수 정보 표시 언어를 입력하세요.
                                입력 가능 값: 한국어 / 中文 / English
                                """;
                    } else {
                        replyText = buildKrText(p);
                    }
                }
            }
        }

        return ResponseEntity.ok(kakaoResponse(replyText));
    }

    // 韩文基准模板
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

    // 中文输出模板
    private String buildCnText(ProfessorVO p) {
        String deptCn = KoreanTranslateUtil1.transHakgwaToCn(p.getHakgwaNameKr());
        String posCn = KoreanTranslateUtil1.transPositionToCn(p.getPositionTitle());
        final String gyeyeolCn = "计算机信息系列";
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

    // 英文输出模板
    private String buildEnText(ProfessorVO p) {
        String deptEn = KoreanTranslateUtil1.transHakgwaToEn(p.getHakgwaNameKr());
        String posEn = KoreanTranslateUtil1.transPositionToEn(p.getPositionTitle());
        final String gyeyeolEn = "Computer Information Division";
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

    // Kakao返回封装
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