package com.example.chatbot;

import java.time.LocalDate;

public class SchoolCalendar2026Vo {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getter / Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // 转换方法
    public static SchoolCalendar2026Vo convert(SchoolCalendar2026 entity, CalendarTranslator translator, String lang) {
        SchoolCalendar2026Vo vo = new SchoolCalendar2026Vo();
        vo.setId(entity.getId());
        vo.setStartDate(entity.getStartDate());
        vo.setEndDate(entity.getEndDate());
        vo.setTitle(translator.translate(entity.getTitle(), lang));
        return vo;
    }
}