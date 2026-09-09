package com.smarthostel.repository;

import com.smarthostel.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentIdOrderByAttendanceDateDescAttendanceTimeDesc(String studentId);

    Optional<Attendance> findByStudentIdAndAttendanceDate(String studentId, LocalDate attendanceDate);

    boolean existsByStudentIdAndAttendanceDate(String studentId, LocalDate attendanceDate);

    long countByStudentIdAndAttendanceStatus(String studentId, String attendanceStatus);

    long countByStudentId(String studentId);
}
