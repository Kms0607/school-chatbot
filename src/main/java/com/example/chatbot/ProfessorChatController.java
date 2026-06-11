package com.example.chatbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class ProfessorChatController {
    private final ProfessorService professorService;

    public ProfessorChatController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping("/chat/professor")
    public ResponseEntity<Map<String, String>> getProfChatInfo(@RequestBody Map<String, Object> req) {
        // 安全读取用户输入
        Map<String, Object> block = new HashMap<>();
        Object blockObj = req.get("block");
        if (blockObj instanceof Map) block = (Map<String, Object>) blockObj;
        String userInput = (String) block.getOrDefault("utterance", "");
        String replyText;

        // 空白输入，返回提示JSON
        if (userInput.isBlank()) {
            replyText = "교수님 성함을 함께 입력해주세요. 예시: 임덕성 교수님";
            Map<String, String> res = new HashMap<>();
            res.put("text", replyText);
            return ResponseEntity.ok(res);
        }

        // 提取纯姓名
        String searchName = userInput.replace(" 교수님", "");
        searchName = searchName.trim();

        // 数据库查询
        Optional<ProfessorVO> data = professorService.getByName(searchName);
        if (data.isEmpty()) {
            replyText = searchName + " 교수님에 대한 정보를 찾을 수 없습니다.";
        } else {
            ProfessorVO p = data.get();
            replyText = String.format("""
%s 교수님 상세 정보
학과：%s
계열：%s
사무실：%s
연락처：%s
이메일：%s
직급：%s
""",
                    p.getNameKr(),
                    p.getHakgwaNameKr(),
                    p.getGyeyeolNameKr(),
                    p.getOfficeRoom()==null?"없음":p.getOfficeRoom(),
                    p.getPhone()==null?"없음":p.getPhone(),
                    p.getEmail()==null?"없음":p.getEmail(),
                    p.getPositionTitle()==null?"없음":p.getPositionTitle()
            );
        }

        // 输出标准JSON
        Map<String, String> res = new HashMap<>();
        res.put("text", replyText);
        return ResponseEntity.ok(res);
    }
}