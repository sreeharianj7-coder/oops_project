-- ==========================================================
-- SMART HOSTEL ATTENDANCE MANAGEMENT SYSTEM
-- Sample Seed Data
-- ==========================================================

USE smart_hostel_attendance;

-- ----------------------------------------------------------
-- 1. Insert Hostels (Adi Shankara Institute Campus Coordinates)
-- ----------------------------------------------------------
INSERT INTO hostels (id, hostel_name, latitude, longitude, allowed_radius, description) VALUES
(1, 'ASIET Main College Hostel', 10.16983000, 76.43574000, 100, 'Main Campus Hostel - Block A, Adi Shankara Institute of Science and Technology'),
(2, 'ASIET Boys Hostel (Block B)', 10.17012000, 76.43615000, 120, 'Senior Boys Hostel, Mattoor-Kalady Campus'),
(3, 'ASIET Ladies Hostel (Block C)', 10.16945000, 76.43522000, 100, 'Womens Hostel, North Wing');

-- ----------------------------------------------------------
-- 2. Insert Sample Students
-- Note: password_hash below corresponds to BCrypt hash of "Password@123"
-- ($2a$10$e8w.n8U7yZ8N8Yl9xS6W9O3F3vW1m0K4lH7G3f2E1D9C8B7A6Z5y4 or standard BCrypt)
-- ----------------------------------------------------------
INSERT INTO students (id, student_id, name, email, phone, course, department, academic_year, hostel_id, room_number, password_hash) VALUES
(1, 'ASIET2024CS001', 'Rahul Sharma', 'rahul.cs@adishankara.ac.in', '+91 98765 43210', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '2023 - 2027', 1, 'A-204', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW'),
(2, 'ASIET2024CS042', 'Ananya Menon', 'ananya.m@adishankara.ac.in', '+91 98451 23456', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '2023 - 2027', 3, 'C-108', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW'),
(3, 'ASIET2024CS089', 'Gautam Nair', 'gautam.n@adishankara.ac.in', '+91 97456 78901', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '2023 - 2027', 2, 'B-312', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW');

-- ----------------------------------------------------------
-- 3. Insert Sample Attendance Records
-- ----------------------------------------------------------
INSERT INTO attendance (student_id, attendance_date, attendance_time, latitude, longitude, distance_from_hostel, location_status, attendance_status, verification_mode) VALUES
('ASIET2024CS001', '2026-09-07', '08:45:12', 10.16985000, 76.43572000, 3.15, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS001', '2026-09-08', '08:42:30', 10.16991000, 76.43580000, 11.20, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS001', '2026-09-09', '08:50:04', 10.16982000, 76.43575000, 1.50, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS042', '2026-09-08', '08:35:19', 10.16947000, 76.43521000, 2.40, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS042', '2026-09-09', '08:40:55', 10.16943000, 76.43525000, 4.10, 'VERIFIED', 'PRESENT', 'GEOLOCATION');
