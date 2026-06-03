package com.example.chatbot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleTextUtil scheduleTextUtil;

    public ScheduleController(ScheduleRepository scheduleRepository, ScheduleTextUtil scheduleTextUtil) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleTextUtil = scheduleTextUtil;
    }

    @GetMapping("/schedule")
    public List<ScheduleVo> getSchedule(
            @RequestParam(name = "lang", defaultValue = "ko") String lang
    ) {
        return scheduleRepository.findAll().stream()
                .map(schedule -> ScheduleVo.getVo(schedule, scheduleTextUtil, lang))
                .collect(Collectors.toList());
    }
}