-- =============================================
-- GearRent Pro Database Setup Script
-- =============================================

CREATE DATABASE IF NOT EXISTS gearrentpro;
USE gearrentpro;

-- =============================================
-- 1. BRANCHES TABLE
-- =============================================
CREATE TABLE branches (
    branch_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. USERS TABLE (System Users: Admin, Manager, Staff)
-- =============================================
CREATE TABLE users (
    user_id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Store hashed password
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'BRANCH_MANAGER', 'STAFF') NOT NULL,
    branch_id VARCHAR(10),           -- NULL for Admin (can see all)
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

-- =============================================
-- 3. CATEGORIES TABLE
-- =============================================
CREATE TABLE categories (
    category_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    base_price_factor DECIMAL(3,2) NOT NULL DEFAULT 1.00,  -- e.g., 1.5 for premium
    weekend_multiplier DECIMAL(3,2) NOT NULL DEFAULT 1.00, -- e.g., 1.2 for weekends
    default_late_fee_per_day DECIMAL(10,2) DEFAULT 500.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. EQUIPMENT TABLE
-- =============================================
CREATE TABLE equipment (
    equipment_id VARCHAR(15) PRIMARY KEY,
    category_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    purchase_year INT NOT NULL,
    daily_base_price DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED', 'RENTED', 'UNDER_MAINTENANCE') DEFAULT 'AVAILABLE',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

-- =============================================
-- 5. MEMBERSHIP LEVELS TABLE
-- =============================================
CREATE TABLE membership_levels (
    level_id VARCHAR(10) PRIMARY KEY,
    level_name ENUM('REGULAR', 'SILVER', 'GOLD') NOT NULL UNIQUE,
    discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. CUSTOMERS TABLE
-- =============================================
CREATE TABLE customers (
    customer_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    nic_passport VARCHAR(20) NOT NULL UNIQUE,
    contact_no VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    membership_level ENUM('REGULAR', 'SILVER', 'GOLD') DEFAULT 'REGULAR',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. RESERVATIONS TABLE
-- =============================================
CREATE TABLE reservations (
    reservation_id VARCHAR(15) PRIMARY KEY,
    equipment_id VARCHAR(15) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    estimated_amount DECIMAL(12,2),
    deposit_amount DECIMAL(10,2),
    status ENUM('ACTIVE', 'CONVERTED', 'CANCELLED', 'EXPIRED') DEFAULT 'ACTIVE',
    created_by VARCHAR(10),          -- User who created
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- =============================================
-- 8. RENTALS TABLE
-- =============================================
CREATE TABLE rentals (
    rental_id VARCHAR(15) PRIMARY KEY,
    reservation_id VARCHAR(15),       -- NULL if direct rental
    equipment_id VARCHAR(15) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    actual_return_date DATE,
    
    -- Pricing details
    base_rental_amount DECIMAL(12,2) NOT NULL,
    weekend_charges DECIMAL(10,2) DEFAULT 0.00,
    long_rental_discount DECIMAL(10,2) DEFAULT 0.00,
    membership_discount DECIMAL(10,2) DEFAULT 0.00,
    final_rental_amount DECIMAL(12,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    
    -- Return details
    late_fee DECIMAL(10,2) DEFAULT 0.00,
    damage_charge DECIMAL(10,2) DEFAULT 0.00,
    damage_description TEXT,
    refund_amount DECIMAL(10,2) DEFAULT 0.00,
    additional_payment DECIMAL(10,2) DEFAULT 0.00,
    
    -- Status
    payment_status ENUM('PAID', 'PARTIALLY_PAID', 'UNPAID') DEFAULT 'UNPAID',
    rental_status ENUM('ACTIVE', 'RETURNED', 'OVERDUE', 'CANCELLED') DEFAULT 'ACTIVE',
    
    created_by VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- =============================================
-- 9. SYSTEM CONFIGURATION TABLE
-- =============================================
CREATE TABLE system_config (
    config_key VARCHAR(50) PRIMARY KEY,
    config_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- INSERT SAMPLE DATA
-- =============================================

-- Branches
INSERT INTO branches (branch_id, name, address, contact) VALUES
('BR001', 'Panadura Branch', '123 Main Street, Panadura', '038-2234567'),
('BR002', 'Galle Branch', '456 Beach Road, Galle', '091-2234567'),
('BR003', 'Colombo Branch', '789 City Center, Colombo 03', '011-2234567');

-- System Users (passwords should be hashed in real application)
INSERT INTO users (user_id, username, password, full_name, email, role, branch_id) VALUES
('U001', 'admin', 'admin123', 'System Admin', 'admin@gearrent.com', 'ADMIN', NULL),
('U002', 'manager_pan', 'manager123', 'Kamal Perera', 'kamal@gearrent.com', 'BRANCH_MANAGER', 'BR001'),
('U003', 'manager_galle', 'manager123', 'Sunil Silva', 'sunil@gearrent.com', 'BRANCH_MANAGER', 'BR002'),
('U004', 'staff_pan1', 'staff123', 'Nimal Fernando', 'nimal@gearrent.com', 'STAFF', 'BR001'),
('U005', 'staff_galle1', 'staff123', 'Kumari Dias', 'kumari@gearrent.com', 'STAFF', 'BR002');

-- Categories
INSERT INTO categories (category_id, name, description, base_price_factor, weekend_multiplier, default_late_fee_per_day) VALUES
('CAT001', 'Camera', 'DSLR and Mirrorless Cameras', 1.00, 1.20, 500.00),
('CAT002', 'Lens', 'Camera Lenses - Various focal lengths', 0.80, 1.15, 400.00),
('CAT003', 'Drone', 'Professional and Consumer Drones', 1.50, 1.30, 1000.00),
('CAT004', 'Lighting', 'Studio and Portable Lighting Equipment', 0.70, 1.10, 300.00),
('CAT005', 'Audio', 'Microphones, Recorders, Audio Gear', 0.60, 1.10, 250.00);

-- Membership Levels
INSERT INTO membership_levels (level_id, level_name, discount_percentage, description) VALUES
('MEM001', 'REGULAR', 0.00, 'Standard membership - No discount'),
('MEM002', 'SILVER', 5.00, 'Silver membership - 5% discount on rentals'),
('MEM003', 'GOLD', 10.00, 'Gold membership - 10% discount on rentals');

-- Equipment (20+ items across branches)
INSERT INTO equipment (equipment_id, category_id, branch_id, brand, model, purchase_year, daily_base_price, security_deposit, status) VALUES
-- Panadura Branch Equipment
('EQ001', 'CAT001', 'BR001', 'Canon', 'EOS R5', 2023, 5000.00, 50000.00, 'AVAILABLE'),
('EQ002', 'CAT001', 'BR001', 'Sony', 'A7 IV', 2022, 4500.00, 45000.00, 'AVAILABLE'),
('EQ003', 'CAT002', 'BR001', 'Canon', 'RF 24-70mm f/2.8', 2023, 2500.00, 30000.00, 'AVAILABLE'),
('EQ004', 'CAT002', 'BR001', 'Sony', 'FE 70-200mm f/2.8', 2022, 3000.00, 35000.00, 'AVAILABLE'),
('EQ005', 'CAT003', 'BR001', 'DJI', 'Mavic 3 Pro', 2023, 8000.00, 80000.00, 'AVAILABLE'),
('EQ006', 'CAT004', 'BR001', 'Godox', 'AD600 Pro', 2022, 1500.00, 20000.00, 'AVAILABLE'),
('EQ007', 'CAT005', 'BR001', 'Rode', 'NTG5', 2023, 1000.00, 15000.00, 'AVAILABLE'),

-- Galle Branch Equipment
('EQ008', 'CAT001', 'BR002', 'Nikon', 'Z8', 2023, 5500.00, 55000.00, 'AVAILABLE'),
('EQ009', 'CAT001', 'BR002', 'Canon', 'EOS R6 II', 2023, 4000.00, 40000.00, 'AVAILABLE'),
('EQ010', 'CAT002', 'BR002', 'Nikon', 'Z 24-120mm f/4', 2022, 2000.00, 25000.00, 'AVAILABLE'),
('EQ011', 'CAT003', 'BR002', 'DJI', 'Inspire 3', 2023, 15000.00, 150000.00, 'AVAILABLE'),
('EQ012', 'CAT004', 'BR002', 'Aputure', '600d Pro', 2022, 2000.00, 25000.00, 'AVAILABLE'),
('EQ013', 'CAT005', 'BR002', 'Sennheiser', 'MKH 416', 2021, 1200.00, 18000.00, 'AVAILABLE'),
('EQ014', 'CAT005', 'BR002', 'Zoom', 'F6', 2022, 800.00, 12000.00, 'AVAILABLE'),

-- Colombo Branch Equipment
('EQ015', 'CAT001', 'BR003', 'Sony', 'FX3', 2023, 6000.00, 60000.00, 'AVAILABLE'),
('EQ016', 'CAT001', 'BR003', 'Blackmagic', 'Pocket 6K Pro', 2022, 4500.00, 45000.00, 'AVAILABLE'),
('EQ017', 'CAT002', 'BR003', 'Sigma', '35mm f/1.4 Art', 2021, 1500.00, 20000.00, 'AVAILABLE'),
('EQ018', 'CAT002', 'BR003', 'Canon', 'RF 100-500mm', 2023, 3500.00, 40000.00, 'AVAILABLE'),
('EQ019', 'CAT003', 'BR003', 'DJI', 'Mini 4 Pro', 2023, 3000.00, 30000.00, 'AVAILABLE'),
('EQ020', 'CAT004', 'BR003', 'Godox', 'VL300', 2022, 1200.00, 15000.00, 'AVAILABLE'),
('EQ021', 'CAT005', 'BR003', 'DJI', 'Mic 2', 2024, 600.00, 8000.00, 'AVAILABLE');

-- Customers (10+ with different membership levels)
INSERT INTO customers (customer_id, name, nic_passport, contact_no, email, address, membership_level) VALUES
('C001', 'Amal Jayasinghe', '901234567V', '077-1234567', 'amal@email.com', '12 Temple Road, Panadura', 'GOLD'),
('C002', 'Priya Mendis', '885678901V', '076-2345678', 'priya@email.com', '45 Beach Lane, Galle', 'SILVER'),
('C003', 'Ruwan de Silva', '951122334V', '071-3456789', 'ruwan@email.com', '78 Main Street, Colombo', 'REGULAR'),
('C004', 'Sachini Fernando', '925566778V', '077-4567890', 'sachini@email.com', '23 Park Avenue, Panadura', 'GOLD'),
('C005', 'Kasun Rathnayake', '881234567V', '076-5678901', 'kasun@email.com', '56 River Road, Galle', 'SILVER'),
('C006', 'Dilini Perera', '947788990V', '071-6789012', 'dilini@email.com', '89 Hill Street, Colombo', 'REGULAR'),
('C007', 'Chamara Wickrama', '911122334V', '077-7890123', 'chamara@email.com', '34 Lake View, Panadura', 'REGULAR'),
('C008', 'Nethmi Silva', '965544332V', '076-8901234', 'nethmi@email.com', '67 Fort Road, Galle', 'SILVER'),
('C009', 'Ishara Gunasekera', '875566778V', '071-9012345', 'ishara@email.com', '12 Garden Lane, Colombo', 'GOLD'),
('C010', 'Tharushi Bandara', '931234567V', '077-0123456', 'tharushi@email.com', '45 Station Road, Panadura', 'REGULAR');

-- System Configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('MAX_DEPOSIT_LIMIT', '500000', 'Maximum total deposit limit per customer in LKR'),
('MAX_RENTAL_DAYS', '30', 'Maximum rental duration in days'),
('LONG_RENTAL_THRESHOLD', '7', 'Days threshold for long rental discount'),
('LONG_RENTAL_DISCOUNT', '10', 'Long rental discount percentage'),
('DEFAULT_LATE_FEE', '500', 'Default late fee per day in LKR');

-- Sample Reservations
INSERT INTO reservations (reservation_id, equipment_id, customer_id, branch_id, start_date, end_date, estimated_amount, deposit_amount, status, created_by) VALUES
('RES001', 'EQ001', 'C001', 'BR001', '2026-04-15', '2026-04-20', 30000.00, 50000.00, 'ACTIVE', 'U004'),
('RES002', 'EQ011', 'C002', 'BR002', '2026-04-18', '2026-04-25', 120000.00, 150000.00, 'ACTIVE', 'U005');

-- Sample Rentals (including some overdue)
INSERT INTO rentals (rental_id, equipment_id, customer_id, branch_id, start_date, end_date, base_rental_amount, final_rental_amount, security_deposit, payment_status, rental_status, created_by) VALUES
('RNT001', 'EQ002', 'C003', 'BR001', '2026-04-01', '2026-04-05', 22500.00, 22500.00, 45000.00, 'PAID', 'ACTIVE', 'U004'),
('RNT002', 'EQ008', 'C005', 'BR002', '2026-04-05', '2026-04-10', 27500.00, 26125.00, 55000.00, 'PAID', 'OVERDUE', 'U005'),
('RNT003', 'EQ015', 'C009', 'BR003', '2026-04-08', '2026-04-15', 42000.00, 37800.00, 60000.00, 'PAID', 'ACTIVE', 'U001');