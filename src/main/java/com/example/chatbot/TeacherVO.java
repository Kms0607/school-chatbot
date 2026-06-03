package com.example.chatbot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TeacherVO {
    private Integer teacherId;
    private String name;
    private String department;
    private String major;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private PortraitInfo portrait;
    private List<ReviewInfo> reviews;

    // 画像信息（字段全部转成文字）
    public static class PortraitInfo {
        private String rollCallLevel;   // 点名情况
        private String homeworkLevel;   // 作业量
        private String examLevel;       // 考试难度
        private BigDecimal attendanceRatio;
        private String styleTag;
        private String riskScore;       // 风险指数

        // Getter/Setter
        public String getRollCallLevel() { return rollCallLevel; }
        public void setRollCallLevel(String rollCallLevel) { this.rollCallLevel = rollCallLevel; }
        public String getHomeworkLevel() { return homeworkLevel; }
        public void setHomeworkLevel(String homeworkLevel) { this.homeworkLevel = homeworkLevel; }
        public String getExamLevel() { return examLevel; }
        public void setExamLevel(String examLevel) { this.examLevel = examLevel; }
        public BigDecimal getAttendanceRatio() { return attendanceRatio; }
        public void setAttendanceRatio(BigDecimal attendanceRatio) { this.attendanceRatio = attendanceRatio; }
        public String getStyleTag() { return styleTag; }
        public void setStyleTag(String styleTag) { this.styleTag = styleTag; }
        public String getRiskScore() { return riskScore; }
        public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    }

    // 评价信息
    public static class ReviewInfo {
        private Integer id;
        private String content;
        private LocalDateTime createTime;
        private Integer isPass;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public Integer getIsPass() { return isPass; }
        public void setIsPass(Integer isPass) { this.isPass = isPass; }
    }

    // 主类 Getter/Setter
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public PortraitInfo getPortrait() { return portrait; }
    public void setPortrait(PortraitInfo portrait) { this.portrait = portrait; }
    public List<ReviewInfo> getReviews() { return reviews; }
    public void setReviews(List<ReviewInfo> reviews) { this.reviews = reviews; }
}