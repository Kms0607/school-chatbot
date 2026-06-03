package com.example.chatbot;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_review")
public class TeacherReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String content;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "is_pass")
    private Integer isPass;

    // Getters
    public Integer getId() { return id; }
    public Teacher getTeacher() { return teacher; }
    public String getContent() { return content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public Integer getIsPass() { return isPass; }
}