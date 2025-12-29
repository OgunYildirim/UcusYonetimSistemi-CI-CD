-- ============================================
-- 1. ROLLER (Backend'in beklediği tam isimler)
-- ============================================
-- Use UPSERT to ensure roles always exist
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_USER', 'Standard user role'),
(2, 'ROLE_ADMIN', 'Administrator role with full access'),
(3, 'ROLE_STAFF', 'Staff member with limited admin access')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;


-- ============================================
-- 2. KULLANICILAR (Şifreler: admin123 ve password123)
-- ============================================
-- admin/admin123
-- john.doe/password123
INSERT INTO users (username, email, password, first_name, last_name, phone_number, enabled, created_at) VALUES
('admin', 'admin@flightmanagement.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQl3MprdWksis7MI7g1T9B3.HO', 'Admin', 'User', '+905551234567', true, CURRENT_TIMESTAMP),
('john.doe', 'john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John', 'Doe', '+905551234568', true, CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane', 'Smith', '+905551234569', true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- ============================================
-- 3. KULLANICI ROLLERİ (Yetkilendirme)
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ROLE_USER
(1, 2), -- admin -> ROLE_ADMIN
(2, 1), -- john.doe -> ROLE_USER
(3, 1)  -- jane.smith -> ROLE_USER
ON CONFLICT DO NOTHING;

-- ============================================
-- 4. HAVALİMANLARI (Selenium Testleri için IST ve SAW kritik)
-- ============================================
INSERT INTO airports (code, name, city, country, address, active) VALUES
('IST', 'Istanbul Airport', 'Istanbul', 'Turkey', 'Arnavutkoy, Istanbul', true),
('SAW', 'Sabiha Gokcen Airport', 'Istanbul', 'Turkey', 'Pendik, Istanbul', true),
('ESB', 'Esenboga Airport', 'Ankara', 'Turkey', 'Cubuk, Ankara', true)
ON CONFLICT DO NOTHING;

-- ============================================
-- 5. UÇAKLAR
-- ============================================
INSERT INTO aircrafts (registration_number, model, manufacturer, total_seats, economy_seats, business_seats, active, under_maintenance, year_of_manufacture) VALUES
('TC-JRO', 'Boeing 737-800', 'Boeing', 180, 162, 18, true, false, 2015),
('TC-JRE', 'Airbus A320', 'Airbus', 174, 156, 18, true, false, 2018)
ON CONFLICT DO NOTHING;

-- ============================================
-- 6. KOLTUKLAR (Uçak 1 için örnek koltuklar)
-- ============================================
INSERT INTO seats (aircraft_id, seat_number, seat_class, is_available, is_window_seat, is_aisle_seat) VALUES
(1, '1A', 'BUSINESS', true, true, false),
(1, '1B', 'BUSINESS', true, false, false),
(1, '10A', 'ECONOMY', true, true, false),
(1, '10B', 'ECONOMY', true, false, false)
ON CONFLICT DO NOTHING;

-- ============================================
-- 7. UÇUŞLAR (Bugünden itibaren 1-3 gün sonrası için)
-- ============================================
INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, aircraft_id, departure_time, arrival_time, available_seats, available_economy_seats, available_business_seats, status) VALUES
('TK001', 1, 2, 1, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day 2 hours', 180, 162, 18, 'SCHEDULED'),
('TK002', 2, 3, 2, CURRENT_TIMESTAMP + INTERVAL '2 day', CURRENT_TIMESTAMP + INTERVAL '2 day 1 hours', 174, 156, 18, 'SCHEDULED')
ON CONFLICT DO NOTHING;

-- ============================================
-- 8. FİYATLANDIRMA
-- ============================================
INSERT INTO flight_pricing (flight_id, economy_price, business_price, baggage_price_per_kg, free_baggage_kg, effective_from, active) VALUES
(1, 500.00, 1500.00, 10.00, 20, CURRENT_TIMESTAMP, true),
(2, 600.00, 1800.00, 12.00, 20, CURRENT_TIMESTAMP, true)
ON CONFLICT DO NOTHING;