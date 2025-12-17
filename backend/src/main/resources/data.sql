-- Database initialization script for Flight Management System
-- This script creates comprehensive test data for all tables

-- ============================================
-- 1. ROLES
-- ============================================
INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'Standard user role'),
('ROLE_ADMIN', 'Administrator role with full access'),
('ROLE_STAFF', 'Staff member with limited admin access');

-- ============================================
-- 2. USERS (Passwords: admin=admin123, others=password123)
-- ============================================
-- BCrypt hash for 'admin123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQl3MprdWksis7MI7g1T9B3.HO
-- BCrypt hash for 'password123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (username, email, password, first_name, last_name, phone_number, enabled, created_at) VALUES
('admin', 'admin@flightmanagement.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQl3MprdWksis7MI7g1T9B3.HO', 'Admin', 'User', '+905551234567', true, CURRENT_TIMESTAMP),
('john.doe', 'john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John', 'Doe', '+905551234568', true, CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane', 'Smith', '+905551234569', true, CURRENT_TIMESTAMP),
('staff.member', 'staff@flightmanagement.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Staff', 'Member', '+905551234570', true, CURRENT_TIMESTAMP),
('alice.wonder', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice', 'Wonder', '+905551234571', true, CURRENT_TIMESTAMP);

-- ============================================
-- 3. USER_ROLES (Many-to-Many)
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin has ROLE_USER
(1, 2), -- admin has ROLE_ADMIN
(2, 1), -- john.doe has ROLE_USER
(3, 1), -- jane.smith has ROLE_USER
(4, 1), -- staff.member has ROLE_USER
(4, 3), -- staff.member has ROLE_STAFF
(5, 1); -- alice.wonder has ROLE_USER

-- ============================================
-- 4. AIRPORTS
-- ============================================
INSERT INTO airports (code, name, city, country, address, active) VALUES
('IST', 'Istanbul Airport', 'Istanbul', 'Turkey', 'Tayakadın, 34283 Arnavutköy/İstanbul', true),
('SAW', 'Sabiha Gökçen International Airport', 'Istanbul', 'Turkey', 'Sanayi, 34906 Pendik/İstanbul', true),
('ESB', 'Esenboğa Airport', 'Ankara', 'Turkey', 'Balıkhisar, 06780 Çubuk/Ankara', true),
('AYT', 'Antalya Airport', 'Antalya', 'Turkey', 'Antalya Havalimanı, 07230 Muratpaşa/Antalya', true),
('ADB', 'Adnan Menderes Airport', 'Izmir', 'Turkey', 'Dokuz Eylül, 35425 Gaziemir/İzmir', true);

-- ============================================
-- 5. AIRCRAFTS
-- ============================================
INSERT INTO aircrafts (registration_number, model, manufacturer, total_seats, economy_seats, business_seats, year_of_manufacture, active, under_maintenance) VALUES
('TC-JRO', 'Boeing 737-800', 'Boeing', 180, 162, 18, 2015, true, false),
('TC-JRE', 'Airbus A320', 'Airbus', 174, 156, 18, 2018, true, false),
('TC-JRK', 'Boeing 777-300ER', 'Boeing', 349, 301, 48, 2017, true, false),
('TC-JRL', 'Airbus A321', 'Airbus', 220, 196, 24, 2019, true, false),
('TC-JRM', 'Boeing 737-900', 'Boeing', 189, 171, 18, 2016, true, true);

-- ============================================
-- 6. SEATS (Sample seats for first aircraft)
-- ============================================
-- Business Class seats (1A-3F) for TC-JRO
INSERT INTO seats (aircraft_id, seat_number, seat_class, is_available, is_window_seat, is_aisle_seat) VALUES
(1, '1A', 'BUSINESS', true, true, false),
(1, '1B', 'BUSINESS', true, false, false),
(1, '1C', 'BUSINESS', true, false, true),
(1, '1D', 'BUSINESS', true, false, true),
(1, '1E', 'BUSINESS', true, false, false),
(1, '1F', 'BUSINESS', true, true, false),
(1, '2A', 'BUSINESS', true, true, false),
(1, '2B', 'BUSINESS', true, false, false),
(1, '2C', 'BUSINESS', true, false, true),
(1, '2D', 'BUSINESS', true, false, true),
(1, '2E', 'BUSINESS', true, false, false),
(1, '2F', 'BUSINESS', true, true, false);

-- Economy Class seats (10A-15F) for TC-JRO
INSERT INTO seats (aircraft_id, seat_number, seat_class, is_available, is_window_seat, is_aisle_seat) VALUES
(1, '10A', 'ECONOMY', true, true, false),
(1, '10B', 'ECONOMY', true, false, false),
(1, '10C', 'ECONOMY', true, false, true),
(1, '10D', 'ECONOMY', true, false, true),
(1, '10E', 'ECONOMY', true, false, false),
(1, '10F', 'ECONOMY', true, true, false),
(1, '11A', 'ECONOMY', true, true, false),
(1, '11B', 'ECONOMY', true, false, false),
(1, '11C', 'ECONOMY', true, false, true),
(1, '11D', 'ECONOMY', true, false, true),
(1, '11E', 'ECONOMY', true, false, false),
(1, '11F', 'ECONOMY', true, true, false);

-- ============================================
-- 7. FLIGHTS
-- ============================================
INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, aircraft_id, departure_time, arrival_time, available_seats, available_economy_seats, available_business_seats, status) VALUES
('TK001', 1, 3, 1, CURRENT_TIMESTAMP + INTERVAL '1 day 08:00', CURRENT_TIMESTAMP + INTERVAL '1 day 09:30', 180, 162, 18, 'SCHEDULED'),
('TK002', 3, 1, 2, CURRENT_TIMESTAMP + INTERVAL '1 day 14:00', CURRENT_TIMESTAMP + INTERVAL '1 day 15:30', 174, 156, 18, 'SCHEDULED'),
('TK003', 1, 4, 3, CURRENT_TIMESTAMP + INTERVAL '2 day 10:00', CURRENT_TIMESTAMP + INTERVAL '2 day 11:30', 349, 301, 48, 'SCHEDULED'),
('TK004', 2, 5, 4, CURRENT_TIMESTAMP + INTERVAL '2 day 16:00', CURRENT_TIMESTAMP + INTERVAL '2 day 17:15', 220, 196, 24, 'SCHEDULED'),
('TK005', 4, 1, 1, CURRENT_TIMESTAMP + INTERVAL '3 day 12:00', CURRENT_TIMESTAMP + INTERVAL '3 day 13:30', 180, 162, 18, 'SCHEDULED');

-- ============================================
-- 8. FLIGHT_PRICING
-- ============================================
INSERT INTO flight_pricing (flight_id, economy_price, business_price, baggage_price_per_kg, free_baggage_kg, effective_from, effective_to, active) VALUES
(1, 500.00, 1500.00, 10.00, 20, CURRENT_TIMESTAMP, NULL, true),
(2, 500.00, 1500.00, 10.00, 20, CURRENT_TIMESTAMP, NULL, true),
(3, 800.00, 2400.00, 15.00, 25, CURRENT_TIMESTAMP, NULL, true),
(4, 600.00, 1800.00, 12.00, 20, CURRENT_TIMESTAMP, NULL, true),
(5, 800.00, 2400.00, 15.00, 25, CURRENT_TIMESTAMP, NULL, true);

-- ============================================
-- 9. BOOKINGS
-- ============================================
INSERT INTO booking (booking_reference, user_id, flight_id, booking_date, number_of_passengers, total_amount, status) VALUES
('BK001234', 2, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', 2, 1000.00, 'CONFIRMED'),
('BK001235', 3, 3, CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 800.00, 'CONFIRMED'),
('BK001236', 5, 2, CURRENT_TIMESTAMP - INTERVAL '3 hours', 3, 1500.00, 'PENDING'),
('BK001237', 2, 4, CURRENT_TIMESTAMP - INTERVAL '5 hours', 1, 600.00, 'CONFIRMED'),
('BK001238', 3, 5, CURRENT_TIMESTAMP - INTERVAL '1 hour', 2, 1600.00, 'PENDING');

-- ============================================
-- 10. TICKETS
-- ============================================
INSERT INTO tickets (ticket_number, booking_id, passenger_first_name, passenger_last_name, passport_number, seat_class, seat_number, ticket_price, status) VALUES
('TK001234001', 1, 'John', 'Doe', 'P1234567', 'ECONOMY', '10A', 500.00, 'CONFIRMED'),
('TK001234002', 1, 'Mary', 'Doe', 'P1234568', 'ECONOMY', '10B', 500.00, 'CONFIRMED'),
('TK001235001', 2, 'Jane', 'Smith', 'P9876543', 'ECONOMY', '15C', 800.00, 'CONFIRMED'),
('TK001236001', 3, 'Alice', 'Wonder', 'P5555555', 'ECONOMY', '12A', 500.00, 'PENDING'),
('TK001236002', 3, 'Bob', 'Wonder', 'P5555556', 'ECONOMY', '12B', 500.00, 'PENDING'),
('TK001236003', 3, 'Charlie', 'Wonder', 'P5555557', 'ECONOMY', '12C', 500.00, 'PENDING'),
('TK001237001', 4, 'John', 'Doe', 'P1234567', 'ECONOMY', '14A', 600.00, 'CONFIRMED'),
('TK001238001', 5, 'Jane', 'Smith', 'P9876543', 'ECONOMY', '16A', 800.00, 'PENDING'),
('TK001238002', 5, 'Sarah', 'Smith', 'P9876544', 'ECONOMY', '16B', 800.00, 'PENDING');

-- ============================================
-- 11. PAYMENTS
-- ============================================
INSERT INTO payments (booking_id, amount, payment_method, payment_date, status, transaction_id, description) VALUES
(1, 1000.00, 'CREDIT_CARD', CURRENT_TIMESTAMP - INTERVAL '2 days', 'COMPLETED', 'TXN001234567', 'Payment for booking BK001234'),
(2, 800.00, 'CREDIT_CARD', CURRENT_TIMESTAMP - INTERVAL '1 day', 'COMPLETED', 'TXN001234568', 'Payment for booking BK001235'),
(4, 600.00, 'DEBIT_CARD', CURRENT_TIMESTAMP - INTERVAL '5 hours', 'COMPLETED', 'TXN001234570', 'Payment for booking BK001237');

-- ============================================
-- 12. BAGGAGE
-- ============================================
INSERT INTO baggage (ticket_id, weight_kg, baggage_type, baggage_tag, status, baggage_fee) VALUES
(1, 20.0, 'CHECKED', 'IST001234', 'CHECKED_IN', 0.00),
(2, 18.5, 'CHECKED', 'IST001235', 'CHECKED_IN', 0.00),
(3, 25.0, 'CHECKED', 'IST001236', 'CHECKED_IN', 50.00),
(7, 22.0, 'CHECKED', 'SAW001237', 'CHECKED_IN', 20.00);

-- ============================================
-- 13. AIRCRAFT_MAINTENANCE
-- ============================================
INSERT INTO aircraft_maintenance (aircraft_id, maintenance_type, description, start_date, end_date, status, cost, performed_by) VALUES
(5, 'SCHEDULED', 'Regular maintenance check - 6 months', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '2 days', 'IN_PROGRESS', 50000.00, 'Turkish Technic'),
(1, 'SCHEDULED', 'Engine inspection', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '25 days', 'COMPLETED', 75000.00, 'Turkish Technic'),
(3, 'UNSCHEDULED', 'Landing gear repair', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '10 days', 'COMPLETED', 120000.00, 'Boeing Service Center'),
(2, 'SCHEDULED', 'Annual safety check', CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '55 days', 'COMPLETED', 45000.00, 'Airbus Service Center'),
(4, 'SCHEDULED', 'Routine inspection', CURRENT_TIMESTAMP - INTERVAL '90 days', CURRENT_TIMESTAMP - INTERVAL '87 days', 'COMPLETED', 35000.00, 'Turkish Technic');
