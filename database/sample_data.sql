-- ==========================================================
-- SMART HOSTEL ATTENDANCE MANAGEMENT SYSTEM
-- Sample Seed Data (MySQL 8.0+)
-- Institution: Adi Shankara Institute of Science and Technology
-- Department: Computer Science and Engineering
-- ==========================================================

USE smart_hostel_attendance;

-- ----------------------------------------------------------
-- 1. Insert Hostels (Adi Shankara Institute Main Campus Coordinates & 1000m Geofence)
-- ----------------------------------------------------------
INSERT INTO hostels (id, hostel_name, latitude, longitude, allowed_radius, description) VALUES
(1, 'Adi Shankara Institute Main Campus Hostel', 10.1706000, 76.4357000, 1000, 'Main Campus Hostel - Block A, Adi Shankara Institute of Science and Technology, Kalady'),
(2, 'ASIET Boys Hostel (Block B)', 10.1708500, 76.4359200, 1000, 'Senior Boys Hostel, Mattoor-Kalady Campus'),
(3, 'ASIET Ladies Hostel (Block C)', 10.1714000, 76.4364000, 1000, 'Womens Hostel, North Wing');

-- ----------------------------------------------------------
-- 2. Insert Sample Students (Includes 1st-Year and Senior Students)
-- Note: password_hash below corresponds to BCrypt hash of "Password@123"
-- ----------------------------------------------------------
INSERT INTO students (id, student_id, name, email, phone, course, department, academic_year, hostel_id, room_number, password_hash) VALUES
(1, 'ASIET2024CS001', 'Rahul Sharma', 'rahul.cs@adishankara.ac.in', '+91 98765 43210', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '3rd Year (2023-2027)', 1, 'A-204', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW'),
(2, 'ASIET2024CS042', 'Ananya Menon', 'ananya.m@adishankara.ac.in', '+91 98451 23456', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '3rd Year (2023-2027)', 3, 'C-108', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW'),
(3, 'ASIET2024CS089', 'Gautam Nair', 'gautam.n@adishankara.ac.in', '+91 97456 78901', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '3rd Year (2023-2027)', 2, 'B-312', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW'),
(4, 'ASIET2026CS001', 'Aditya Varma', 'aditya.cs26@adishankara.ac.in', '+91 98123 45678', 'B.Tech Computer Science and Engineering', 'Computer Science and Engineering', '1st Year (2026-2030)', 1, 'A-102', '$2a$10$wEeVz9FkX1rB/UfU8l1tfeQJkWcZZZk.4p8d6mG2qR5g9uT1.eJcW');

-- ----------------------------------------------------------
-- 3. Insert Sample Attendance Records
-- ----------------------------------------------------------
INSERT INTO attendance (student_id, attendance_date, attendance_time, latitude, longitude, distance_from_hostel, location_status, attendance_status, verification_mode) VALUES
('ASIET2024CS001', '2026-09-07', '08:45:12', 10.1706200, 76.4357150, 3.15, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS001', '2026-09-08', '08:42:30', 10.1705900, 76.4356800, 11.20, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS001', '2026-09-09', '08:50:04', 10.1706100, 76.4357200, 1.50, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS042', '2026-09-08', '08:35:19', 10.1714200, 76.4363900, 2.40, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2024CS042', '2026-09-09', '08:40:55', 10.1713800, 76.4364200, 4.10, 'VERIFIED', 'PRESENT', 'GEOLOCATION'),
('ASIET2026CS001', '2026-09-09', '09:30:00', 10.1706050, 76.4357020, 0.60, 'VERIFIED', 'PRESENT', 'GEOLOCATION');
