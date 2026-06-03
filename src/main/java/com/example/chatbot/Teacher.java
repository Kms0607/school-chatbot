package com.example.chatbot;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;

    private String name;
    private String department;
    private String major;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 1:1 关联教师画像
    @OneToOne(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private TeacherPortrait portrait;

    // 1:N 关联教师评价
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherReview> reviews;

    // Getters
    public Integer getTeacherId() { return teacherId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getMajor() { return major; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public TeacherPortrait getPortrait() { return portrait; }
    public List<TeacherReview> getReviews() { return reviews; }
}