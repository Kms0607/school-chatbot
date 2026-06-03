package com.example.chatbot;
import jakarta.persistence.*;
import java.time.LocalDateTime;  // 这行我帮你加好

@Entity
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ↓↓↓↓ 你原来的代码 完全不动 ↓↓↓↓
    private int grade;
    private String className;
    private String subjectName;
    private String professorName;
    private String dayOfWeek;
    private int startPeriod;
    private int endPeriod;
    private String roomNumber;

    // ↓↓↓↓ 只在这里 新加这4行 不动别的 ↓↓↓↓
    private String classRoom;
    private String teacherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer courseId;
    private Integer roomId;
    private Integer week;
    private Integer section;

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    // ↓↓↓↓ 你原来的 getter 完全不动 ↓↓↓↓
    public Long getId() { return id; }
    public int getGrade() { return grade; }
    public String getClassName() { return className; }
    public String getSubjectName() { return subjectName; }
    public String getProfessorName() { return professorName; }
    public String getDayOfWeek() { return dayOfWeek; }
    public int getStartPeriod() { return startPeriod; }
    public int getEndPeriod() { return endPeriod; }
    public String getRoomNumber() { return roomNumber; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getClassRoom() {
        return classRoom;
    }
}



