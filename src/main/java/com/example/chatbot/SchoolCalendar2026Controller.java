package com.example.chatbot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SchoolCalendar2026Controller {

    private final SchoolCalendar2026Repository calendarRepository;
    private final CalendarTranslator translateUtil;

    public SchoolCalendar2026Controller(SchoolCalendar2026Repository calendarRepository, CalendarTranslator translateUtil) {
        this.calendarRepository = calendarRepository;
        this.translateUtil = translateUtil;
    }

    @GetMapping("/calendar/2026")
    public List<SchoolCalendar2026Vo> get2026Calendar(
            @RequestParam(name = "lang", defaultValue = "ko") String lang
    ) {
        return calendarRepository.findAll().stream()
                .map(entity -> SchoolCalendar2026Vo.convert(entity, translateUtil, lang))
                .collect(Collectors.toList());
    }
}