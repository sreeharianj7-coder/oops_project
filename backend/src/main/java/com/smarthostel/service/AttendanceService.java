package com.smarthostel.service;

import com.smarthostel.dto.LocationVerifyRequest;
import com.smarthostel.dto.LocationVerifyResponse;
import com.smarthostel.dto.MarkAttendanceRequest;
import com.smarthostel.model.Attendance;
import com.smarthostel.model.Student;
import com.smarthostel.repository.AttendanceRepository;
import com.smarthostel.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * ATTENDANCE SERVICE (Business Logic & Verification Workflow Layer)
 * ============================================================================
 * 
 * Coordinates 1st-year time restriction validation, location verification,
 * duplicate check, and attendance persistence.
 */
@Service
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    // 1st-Year attendance window policy: 09:00 AM to 05:00 PM (17:00)
    public static final LocalTime FIRST_YEAR_START_TIME = LocalTime.of(9, 0, 0);
    public static final LocalTime FIRST_YEAR_END_TIME = LocalTime.of(17, 0, 0);

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final LocationService locationService;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository, 
                             StudentRepository studentRepository, 
                             LocationService locationService) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.locationService = locationService;
    }

    /**
     * Validates whether the student is allowed to mark attendance at the given time.
     * Enforces institutional policy: 1st-year students are restricted to marking
     * attendance exclusively within the 9:00 AM to 5:00 PM time window.
     * 
     * @param student The student attempting to mark attendance
     * @param attendanceTime The current timestamp
     * @throws IllegalArgumentException if 1st-year student attempts marking outside the allowed window
     */
    public void validateTimeWindowRestriction(Student student, LocalTime attendanceTime) {
        if (student != null && student.isFirstYear()) {
            if (attendanceTime.isBefore(FIRST_YEAR_START_TIME) || attendanceTime.isAfter(FIRST_YEAR_END_TIME)) {
                log.warn("Attendance rejected for 1st-year student {}: Attempted at {} outside allowed window (09:00 AM - 05:00 PM)",
                        student.getStudentId(), attendanceTime);
                throw new IllegalArgumentException(
                        "Attendance restriction: 1st-year students are only permitted to mark attendance between 09:00 AM and 05:00 PM. (Current time: " 
                        + attendanceTime.withNano(0) + ")"
                );
            }
        }
    }

    /**
     * Records verified attendance for a student (using system clock).
     * 
     * @param request Mark attendance request with GPS coordinates
     * @return Saved Attendance entity
     */
    @Transactional
    public Attendance markAttendance(MarkAttendanceRequest request) {
        return markAttendance(request, LocalDate.now(), LocalTime.now());
    }

    /**
     * Overloaded method accepting explicit date and time for testability and deterministic validation.
     * 
     * @param request Mark attendance request with GPS coordinates
     * @param today Attendance date
     * @param now Attendance time
     * @return Saved Attendance entity
     */
    @Transactional
    public Attendance markAttendance(MarkAttendanceRequest request, LocalDate today, LocalTime now) {
        String studentId = request.getStudentId().trim().toUpperCase();

        log.info("Attempting to mark attendance for Student: {} on Date: {} at Time: {}", studentId, today, now);

        // 1. Verify Student existence
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // 2. Enforce 1st-Year Time Window Restriction (9:00 AM to 5:00 PM)
        validateTimeWindowRestriction(student, now);

        // 3. Prevent duplicate daily attendance
        if (attendanceRepository.existsByStudentIdAndAttendanceDate(studentId, today)) {
            throw new IllegalStateException("Attendance has already been marked for today (" + today + ")");
        }

        // 4. Perform authoritative server-side location verification
        LocationVerifyRequest verifyReq = new LocationVerifyRequest(
                studentId, request.getLatitude(), request.getLongitude(), request.getAccuracy()
        );
        LocationVerifyResponse verifyRes = locationService.verifyLocation(verifyReq);

        if (!verifyRes.isVerified()) {
            log.warn("Attendance rejected for Student {}: Distance {}m exceeds allowed radius {}m", 
                    studentId, verifyRes.getDistance(), verifyRes.getAllowedRadius());
            throw new IllegalArgumentException("Attendance Failed — You are outside the permitted hostel location (" + 
                    verifyRes.getHostelName() + "). Distance: " + verifyRes.getDistance() + "m, Allowed: " + verifyRes.getAllowedRadius() + "m.");
        }

        // 5. Save Attendance record
        Attendance attendance = new Attendance(
                studentId,
                today,
                now,
                request.getLatitude(),
                request.getLongitude(),
                verifyRes.getDistance(),
                "VERIFIED",
                "PRESENT",
                "GEOLOCATION"
        );

        Attendance savedRecord = attendanceRepository.save(attendance);
        log.info("Attendance marked successfully. ID: {}, Student: {}, Time: {}", 
                savedRecord.getId(), studentId, savedRecord.getAttendanceTime());

        return savedRecord;
    }

    /**
     * Retrieves attendance history for the logged-in student.
     */
    public List<Attendance> getAttendanceHistory(String studentId) {
        return attendanceRepository.findByStudentIdOrderByAttendanceDateDescAttendanceTimeDesc(studentId.trim().toUpperCase());
    }

    /**
     * Retrieves today's attendance status for a student.
     */
    public Optional<Attendance> getTodayAttendance(String studentId) {
        return attendanceRepository.findByStudentIdAndAttendanceDate(studentId.trim().toUpperCase(), LocalDate.now());
    }

    /**
     * Computes dashboard attendance statistics for a student.
     */
    public Map<String, Object> getStudentAttendanceStats(String studentId) {
        String cleanId = studentId.trim().toUpperCase();
        long totalRecords = attendanceRepository.countByStudentId(cleanId);
        long presentRecords = attendanceRepository.countByStudentIdAndAttendanceStatus(cleanId, "PRESENT");

        double percentage = totalRecords > 0 ? ((double) presentRecords / totalRecords) * 100.0 : 100.0;
        percentage = Math.round(percentage * 10.0) / 10.0;

        Optional<Attendance> todayOpt = getTodayAttendance(cleanId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", totalRecords);
        stats.put("presentDays", presentRecords);
        stats.put("absentDays", totalRecords - presentRecords);
        stats.put("attendancePercentage", percentage);
        stats.put("todayMarked", todayOpt.isPresent());
        stats.put("todayAttendance", todayOpt.orElse(null));
        stats.put("todayStatusBadge", todayOpt.isPresent() ? "ATTENDANCE MARKED" : "NOT MARKED");

        return stats;
    }
}
