package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.Student;
import com.smarthostel.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * STUDENT CONTROLLER (REST API for Student & College Profiles)
 * ============================================================================
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Value("${app.college.name:Adi Shankara Institute of Science and Technology}")
    private String collegeName;

    @Value("${app.college.program:B.Tech Computer Science and Engineering}")
    private String programName;

    @Value("${app.college.department:Computer Science and Engineering}")
    private String departmentName;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieves student profile by studentId.
     * GET /api/student/profile?studentId=ASIET2024CS001
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(@RequestParam String studentId) {
        Optional<Student> studentOpt = studentService.findByStudentId(studentId.trim());
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Student not found with ID: " + studentId));
        }

        Student student = studentOpt.get();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("student", student);
        responseData.put("college", Map.of(
                "collegeName", collegeName,
                "program", programName,
                "department", departmentName
        ));

        return ResponseEntity.ok(ApiResponse.ok("Student profile loaded successfully", responseData));
    }

    /**
     * Retrieves static institution details.
     * GET /api/student/college-details
     */
    @GetMapping("/college-details")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCollegeDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("collegeName", collegeName);
        details.put("program", programName);
        details.put("department", departmentName);
        details.put("accreditation", "NAAC Accredited, Affiliated to APJ Abdul Kalam Technological University");
        details.put("location", "Mattoor, Kalady, Ernakulam, Kerala - 683574");

        return ResponseEntity.ok(ApiResponse.ok("College details retrieved", details));
    }
}
