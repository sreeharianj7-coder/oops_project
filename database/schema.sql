-- ==========================================================
-- SMART HOSTEL ATTENDANCE MANAGEMENT SYSTEM
-- Database Schema Definition (MySQL)
-- College: Adi Shankara Institute of Science and Technology
-- ==========================================================

CREATE DATABASE IF NOT EXISTS smart_hostel_attendance;
USE smart_hostel_attendance;

-- ----------------------------------------------------------
-- Table: hostels
-- Stores hostel location coordinates and permitted radius
-- ----------------------------------------------------------
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS hostels;

CREATE TABLE hostels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hostel_name VARCHAR(150) NOT NULL UNIQUE,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    allowed_radius INT NOT NULL DEFAULT 1000 COMMENT 'Allowed radius in meters (default 1000m for 1km geofence)',
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Table: students
-- Stores student profile, academic, and authentication data
-- ----------------------------------------------------------
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    course VARCHAR(100) NOT NULL DEFAULT 'B.Tech Computer Science and Engineering',
    department VARCHAR(100) NOT NULL DEFAULT 'Computer Science and Engineering',
    academic_year VARCHAR(20) NOT NULL DEFAULT '2023 - 2027',
    hostel_id BIGINT NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_hostel FOREIGN KEY (hostel_id) 
        REFERENCES hostels(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Table: attendance
-- Stores verified geolocation-based daily attendance logs
-- ----------------------------------------------------------
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_time TIME NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    distance_from_hostel DECIMAL(8, 2) NOT NULL COMMENT 'Distance in meters',
    location_status ENUM('VERIFIED', 'OUTSIDE_HOSTEL', 'LOCATION_UNVERIFIED') NOT NULL DEFAULT 'VERIFIED',
    attendance_status ENUM('PRESENT', 'ABSENT', 'REJECTED') NOT NULL DEFAULT 'PRESENT',
    verification_mode VARCHAR(50) NOT NULL DEFAULT 'GEOLOCATION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) 
        REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_student_daily_attendance UNIQUE (student_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Helpful Indexes for Performance
-- ----------------------------------------------------------
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_student_id ON students(student_id);
CREATE INDEX idx_attendance_student_date ON attendance(student_id, attendance_date);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
