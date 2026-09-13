package com.smarthostel.config;

import com.smarthostel.model.Attendance;
import com.smarthostel.model.Hostel;
import com.smarthostel.model.Student;
import com.smarthostel.repository.AttendanceRepository;
import com.smarthostel.repository.HostelRepository;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ============================================================================
 * DATA INITIALIZER (Automatic Database Seeding on Startup)
 * ============================================================================
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final HostelRepository hostelRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public DataInitializer(HostelRepository hostelRepository, 
                           StudentRepository studentRepository, 
                           AttendanceRepository attendanceRepository) {
        this.hostelRepository = hostelRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Checking database initialization status...");

        // 1. Initialize Hostels if empty (Main campus geofence: 1000m / 1km radius)
        if (hostelRepository.count() == 0) {
            log.info("Seeding default Adi Shankara Institute hostels with 1000m geofence radius...");
            Hostel mainHostel = new Hostel(
                    "Adi Shankara Institute Main Campus Hostel", 
                    10.1706000, 
                    76.4357000, 
                    1000, 
                    "Main Campus Hostel - Block A, Adi Shankara Institute of Science and Technology, Kalady"
            );
            Hostel boysHostel = new Hostel(
                    "ASIET Boys Hostel (Block B)", 
                    10.1708500, 
                    76.4359200, 
                    1000, 
                    "Senior Boys Hostel, Mattoor-Kalady Campus"
            );
            Hostel ladiesHostel = new Hostel(
                    "ASIET Ladies Hostel (Block C)", 
                    10.1714000, 
                    76.4364000, 
                    1000, 
                    "Womens Hostel, North Wing"
            );

            hostelRepository.save(mainHostel);
            hostelRepository.save(boysHostel);
            hostelRepository.save(ladiesHostel);
            log.info("Hostels initialized successfully with 1000m geofence.");
        }

        // 2. Initialize Sample Students if empty (includes 1st-year and senior students)
        if (studentRepository.count() == 0) {
            log.info("Seeding demo students...");
            Hostel defaultHostel = hostelRepository.findAll().get(0);

            // Password is "Password@123"
            String defaultHash = PasswordHasher.hash("Password@123");

            Student student1 = new Student(
                    "ASIET2024CS001",
                    "Rahul Sharma",
                    "rahul.cs@adishankara.ac.in",
                    "+91 98765 43210",
                    "B.Tech Computer Science and Engineering",
                    "Computer Science and Engineering",
                    "3rd Year (2023-2027)",
                    defaultHostel,
                    "A-204",
                    defaultHash
            );

            Student student2 = new Student(
                    "ASIET2024CS042",
                    "Ananya Menon",
                    "ananya.m@adishankara.ac.in",
                    "+91 98451 23456",
                    "B.Tech Computer Science and Engineering",
                    "Computer Science and Engineering",
                    "3rd Year (2023-2027)",
                    defaultHostel,
                    "C-108",
                    defaultHash
            );

            Student student3 = new Student(
                    "ASIET2026CS001",
                    "Aditya Varma",
                    "aditya.cs26@adishankara.ac.in",
                    "+91 98123 45678",
                    "B.Tech Computer Science and Engineering",
                    "Computer Science and Engineering",
                    "1st Year (2026-2030)",
                    defaultHostel,
                    "A-102",
                    defaultHash
            );

            studentRepository.save(student1);
            studentRepository.save(student2);
            studentRepository.save(student3);

            // Seed sample past attendance
            Attendance record1 = new Attendance(
                    "ASIET2024CS001",
                    LocalDate.now().minusDays(2),
                    LocalTime.of(8, 45, 10),
                    10.170620,
                    76.435715,
                    2.1,
                    "VERIFIED",
                    "PRESENT",
                    "GEOLOCATION"
            );

            Attendance record2 = new Attendance(
                    "ASIET2024CS001",
                    LocalDate.now().minusDays(1),
                    LocalTime.of(8, 41, 22),
                    10.170590,
                    76.435680,
                    8.4,
                    "VERIFIED",
                    "PRESENT",
                    "GEOLOCATION"
            );

            Attendance record3 = new Attendance(
                    "ASIET2026CS001",
                    LocalDate.now().minusDays(1),
                    LocalTime.of(9, 30, 0),
                    10.170605,
                    76.435702,
                    0.6,
                    "VERIFIED",
                    "PRESENT",
                    "GEOLOCATION"
            );

            attendanceRepository.save(record1);
            attendanceRepository.save(record2);
            attendanceRepository.save(record3);

            log.info("Sample students and historical attendance records seeded successfully.");
        }
    }
}
