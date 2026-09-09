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
 * Coordinates location verification, duplicate check, and attendance recording.
 */
@Service
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

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
     * Records verified attendance for a student.
     * 
     * @param request Mark attendance request with GPS coordinates
     * @return Saved Attendance entity
     */
    @Transactional
    public Attendance markAttendance(MarkAttendanceRequest request) {
        String studentId = request.getStudentId().trim().toUpperCase();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        log.info("Attempting to mark attendance for Student: {} on Date: {}", studentId, today);

        // 1. Verify Student existence
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // 2. Prevent duplicate daily attendance
        if (attendanceRepository.existsByStudentIdAndAttendanceDate(studentId, today)) {
            throw new IllegalStateException("Attendance has already been marked for today (" + today + ")");
        }

        // 3. Perform authoritative server-side location verification
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

        // 4. Save Attendance record
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
