package com.example.chatbot;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_portrait")
public class TeacherPortrait {

    @Id
    private Integer teacherId;

    @Column(name = "roll_call_level")
    private Integer rollCallLevel;

    @Column(name = "homework_level")
    private Integer homeworkLevel;

    @Column(name = "exam_level")
    private Integer examLevel;

    @Column(name = "attendance_ratio")
    private BigDecimal attendanceRatio;

    @Column(name = "style_tag")
    private String styleTag;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @OneToOne
    @MapsId
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Getters
    public Integer getTeacherId() { return teacherId; }
    public Integer getRollCallLevel() { return rollCallLevel; }
    public Integer getHomeworkLevel() { return homeworkLevel; }
    public Integer getExamLevel() { return examLevel; }
    public BigDecimal getAttendanceRatio() { return attendanceRatio; }
    public String getStyleTag() { return styleTag; }
    public Integer getRiskScore() { return riskScore; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public Teacher getTeacher() { return teacher; }
}