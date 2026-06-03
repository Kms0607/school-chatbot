package com.example.chatbot;

public class TimetableVo {

    private String subjectName;
    private String professorName;
    private String dayOfWeek;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public static TimetableVo getVo(
            Timetable timetable,
            KoreanTranslateUtil util,
            String lang
    ) {
        TimetableVo vo = new TimetableVo();

        vo.setSubjectName(
                util.translateSubject(timetable.getSubjectName(), lang)
        );

        vo.setProfessorName(
                util.translateProfessor(timetable.getProfessorName(), lang)
        );

        vo.setDayOfWeek(
                util.translateWeek(timetable.getDayOfWeek(), lang)
        );
        vo.setClassName(timetable.getClassName());

        return vo;

    }
}