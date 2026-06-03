package com.example.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class TimetableService {

    @Autowired
    private TimetableRepository timetableRepository;

    // 1. 查询指定时间段内已被占用的教室
    public List<String> getUsedClassrooms(LocalDateTime startTime, LocalDateTime endTime) {
        return timetableRepository.getUsedClassRoom(startTime, endTime);
    }

    // 2. 查询指定时间段内已被占用的教师ID
    public List<String> getUsedTeacherIds(LocalDateTime startTime, LocalDateTime endTime) {
        return timetableRepository.getUsedTeacherId(startTime, endTime);
    }

    // 3. 新增课程排课前的冲突校验（教室+教师双重校验）
    public boolean isClassroomAndTeacherAvailable(LocalDateTime startTime, LocalDateTime endTime,
                                                  String classroom, String teacherId) {
        // 校验教室是否被占用
        List<String> usedClassrooms = getUsedClassrooms(startTime, endTime);
        if (usedClassrooms.contains(classroom)) {
            return false;
        }
        // 校验教师是否被占用
        List<String> usedTeachers = getUsedTeacherIds(startTime, endTime);
        if (usedTeachers.contains(teacherId)) {
            return false;
        }
        // 都没被占用，返回可用
        return true;
    }

    // 4. 新增课程（排课）
    public Timetable addTimetable(Timetable timetable) {
        // 先做冲突校验
        boolean available = isClassroomAndTeacherAvailable(
                timetable.getStartTime(),
                timetable.getEndTime(),
                timetable.getClassRoom(),
                timetable.getTeacherId()
        );
        if (!available) {
            throw new RuntimeException("해당 시간대에 교실이나 교사가 이미 사용 중이므로 수업을 배정할 수 없습니다!");
        }
        // 校验通过，保存课程
        return timetableRepository.save(timetable);
    }

    // 保留你原有的查询方法
    public List<Timetable> getTimetableByGradeAndClass(int grade, String className) {
        return timetableRepository.findByGradeAndClassName(grade, className);

    }
    public Map<String, Object> addSchedule(Timetable timetable) {
        Map<String, Object> result = new HashMap<>();
        // 1. 调用刚才写的Repository方法做双重冲突校验
        int teacherConflict = timetableRepository.countTeacherConflict(
                timetable.getTeacherId(), timetable.getWeek(), timetable.getSection());
        int roomConflict = timetableRepository.countRoomConflict(
                timetable.getRoomId(), timetable.getWeek(), timetable.getSection());

        // 2. 判断冲突
        if (teacherConflict > 0 || roomConflict > 0) {
            result.put("code", 500);
            result.put("msg", "수업 시간 배정 실패: 해당 시간대에 교사나 강의실이 이미 사용 중입니다.");
            // 这里可以加推荐空闲时段的逻辑，先返回冲突提示就行
            return result;
        }

        // 3. 无冲突直接保存入库
        timetableRepository.save(timetable);
        result.put("code", 200);
        result.put("msg", "수업 시간표 등록이 성공적으로 완료되었으며, 데이터가 timetable 테이블에 저장되었습니다.");
        result.put("data", timetable);
        return result;
    }
}