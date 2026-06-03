package com.example.chatbot;

import java.time.LocalDate;

public class ScheduleVo {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public static ScheduleVo getVo(Schedule schedule, ScheduleTextUtil util, String lang) {
        ScheduleVo vo = new ScheduleVo();
        vo.setId(schedule.getId());
        vo.setStartDate(schedule.getStartDate());
        vo.setEndDate(schedule.getEndDate());

        // 核心逻辑：lang=ko 时，直接返回数据库里的韩文
        if ("ko".equals(lang)) {
            vo.setTitle(schedule.getTitle());
        } else {
            // 其他语言走翻译工具
            vo.setTitle(util.translate(schedule.getTitle(), lang));
        }

        return vo;
    }
}