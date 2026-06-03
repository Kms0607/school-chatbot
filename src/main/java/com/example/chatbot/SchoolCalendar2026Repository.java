package com.example.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SchoolCalendar2026Repository extends JpaRepository<SchoolCalendar2026, Long> {

    @Query("SELECT c FROM SchoolCalendar2026 c WHERE MONTH(c.startDate) = :month")
    List<SchoolCalendar2026> findByMonth(@Param("month") int month);

    List<SchoolCalendar2026> findAllByOrderByStartDateAsc();
}