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

        // 1. Initialize Hostels if empty
        if (hostelRepository.count() == 0) {
            log.info("Seeding default Adi Shankara Institute hostels...");
            Hostel mainHostel = new Hostel(
                    "ASIET Main College Hostel", 
                    10.16983000, 
                    76.43574000, 
                    100, 
                    "Main Campus Hostel - Block A, Adi Shankara Institute of Science and Technology"
            );
            Hostel boysHostel = new Hostel(
                    "ASIET Boys Hostel (Block B)", 
                    10.17012000, 
                    76.43615000, 
                    120, 
                    "Senior Boys Hostel, Mattoor-Kalady Campus"
            );
            Hostel ladiesHostel = new Hostel(
                    "ASIET Ladies Hostel (Block C)", 
                    10.16945000, 
                    76.43522000, 
                    100, 
                    "Womens Hostel, North Wing"
            );

            hostelRepository.save(mainHostel);
            hostelRepository.save(boysHostel);
            hostelRepository.save(ladiesHostel);
            log.info("Hostels initialized successfully.");
        }

        // 2. Initialize Sample Student if empty
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
                    "2023 - 2027",
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
                    "2023 - 2027",
                    defaultHostel,
                    "C-108",
                    defaultHash
            );

            studentRepository.save(student1);
            studentRepository.save(student2);

            // Seed sample past attendance
            Attendance record1 = new Attendance(
                    "ASIET2024CS001",
                    LocalDate.now().minusDays(2),
                    LocalTime.of(8, 45, 10),
                    10.169840,
                    76.435730,
                    2.1,
                    "VERIFIED",
                    "PRESENT",
                    "GEOLOCATION"
            );

            Attendance record2 = new Attendance(
                    "ASIET2024CS001",
                    LocalDate.now().minusDays(1),
                    LocalTime.of(8, 41, 22),
                    10.169880,
                    76.435790,
                    8.4,
                    "VERIFIED",
                    "PRESENT",
                    "GEOLOCATION"
            );

            attendanceRepository.save(record1);
            attendanceRepository.save(record2);

            log.info("Sample students and historical attendance records seeded successfully.");
        }
    }
}
