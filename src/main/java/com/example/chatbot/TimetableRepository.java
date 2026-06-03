package com.example.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // 你原来的代码，完全保留！
    List<Timetable> findByGradeAndClassName(int grade, String className);

    // ========================
    // 只在下面新增这两个方法！
    // ========================
    @Query("SELECT DISTINCT t.classRoom FROM Timetable t WHERE t.startTime < :end AND t.endTime > :start")
    List<String> getUsedClassRoom(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT t.teacherId FROM Timetable t WHERE t.startTime < :end AND t.endTime > :start")
    List<String> getUsedTeacherId(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 校验教师同一星期、节次是否有课
    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.teacherId = :teacherId AND t.week = :week AND t.section = :section")
    int countTeacherConflict(@Param("teacherId") String teacherId, @Param("week") Integer week, @Param("section") Integer section);

    // 校验教室同一星期、节次是否有课
    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.roomId = :roomId AND t.week = :week AND t.section = :section")
    int countRoomConflict(@Param("roomId") Integer roomId, @Param("week") Integer week, @Param("section") Integer section);

}