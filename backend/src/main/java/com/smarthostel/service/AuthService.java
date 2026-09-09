package com.smarthostel.service;

import com.smarthostel.dto.AuthResponse;
import com.smarthostel.dto.LoginRequest;
import com.smarthostel.dto.RegisterRequest;
import com.smarthostel.model.Hostel;
import com.smarthostel.model.Student;
import com.smarthostel.repository.HostelRepository;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * ============================================================================
 * AUTHENTICATION SERVICE (Registration, Login & Credential Security Layer)
 * ============================================================================
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final StudentRepository studentRepository;
    private final HostelRepository hostelRepository;

    @Autowired
    public AuthService(StudentRepository studentRepository, HostelRepository hostelRepository) {
        this.studentRepository = studentRepository;
        this.hostelRepository = hostelRepository;
    }

    /**
     * Registers a new student into the system after rigorous validation.
     * 
     * @param request Registration form data
     * @return AuthResponse containing token and student profile
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for Student ID: {}, Email: {}", request.getStudentId(), request.getEmail());

        // 1. Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // 2. Check for duplicate Student ID
        if (studentRepository.existsByStudentId(request.getStudentId().trim())) {
            throw new IllegalArgumentException("Student ID '" + request.getStudentId() + "' is already registered");
        }

        // 3. Check for duplicate Email
        if (studentRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email address '" + request.getEmail() + "' is already in use");
        }

        // 4. Validate Hostel selection
        Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new IllegalArgumentException("Selected hostel not found (ID: " + request.getHostelId() + ")"));

        // 5. Hash password with BCrypt
        String hashedPassword = PasswordHasher.hash(request.getPassword());

        // 6. Build and persist Student entity
        Student student = new Student(
                request.getStudentId().trim().toUpperCase(),
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPhone().trim(),
                request.getCourse() != null ? request.getCourse().trim() : "B.Tech Computer Science and Engineering",
                request.getDepartment() != null ? request.getDepartment().trim() : "Computer Science and Engineering",
                request.getAcademicYear() != null ? request.getAcademicYear().trim() : "2023 - 2027",
                hostel,
                request.getRoomNumber().trim(),
                hashedPassword
        );

        Student savedStudent = studentRepository.save(student);
        log.info("Student successfully registered with ID: {}", savedStudent.getId());

        String sessionToken = "token_" + UUID.randomUUID().toString();
        return new AuthResponse(sessionToken, savedStudent, "Account created successfully");
    }

    /**
     * Authenticates student credentials against the database.
     * 
     * @param request Login credentials (identifier + password)
     * @return AuthResponse with session token
     */
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().trim();
        log.info("Authenticating user with identifier: {}", identifier);

        // Search by studentId first, then by email
        Optional<Student> studentOpt = studentRepository.findByStudentId(identifier.toUpperCase());
        if (studentOpt.isEmpty()) {
            studentOpt = studentRepository.findByEmail(identifier.toLowerCase());
        }

        if (studentOpt.isEmpty()) {
            log.warn("Login failed: Identifier '{}' not found", identifier);
            throw new IllegalArgumentException("Invalid Student ID/Email or Password.");
        }

        Student student = studentOpt.get();

        // Verify BCrypt password hash
        if (!PasswordHasher.verify(request.getPassword(), student.getPasswordHash())) {
            log.warn("Login failed: Password mismatch for student '{}'", student.getStudentId());
            throw new IllegalArgumentException("Invalid Student ID/Email or Password.");
        }

        log.info("Login successful for student: {}", student.getStudentId());
        String sessionToken = "token_" + UUID.randomUUID().toString();
        return new AuthResponse(sessionToken, student, "Login successful");
    }
}
