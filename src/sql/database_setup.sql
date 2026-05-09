CREATE DATABASE IF NOT EXISTS gearrentpro;
USE gearrentpro;

-- =================== TABLE CREATION ===================

-- BRANCH TABLE
CREATE TABLE branch (
    branch_id VARCHAR(10) PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact VARCHAR(20) NOT NULL
);

-- CATEGORY TABLE
CREATE TABLE category (
    category_id VARCHAR(10) PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    base_price_factor DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    weekend_multiplier DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    late_fee_per_day DECIMAL(10,2) NOT NULL DEFAULT 500.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- EQUIPMENT TABLE
CREATE TABLE equipment (
    equipment_id VARCHAR(10) PRIMARY KEY,
    category_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    purchase_year INT NOT NULL,
    daily_base_price DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    status ENUM('Available','Reserved','Rented','Under Maintenance') NOT NULL DEFAULT 'Available',
    FOREIGN KEY (category_id) REFERENCES category(category_id),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- USER TABLE
CREATE TABLE user (
    user_id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('Admin','Branch Manager','Staff') NOT NULL,
    branch_id VARCHAR(10),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- CUSTOMER TABLE
CREATE TABLE customer (
    customer_id VARCHAR(10) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    nic VARCHAR(20) NOT NULL UNIQUE,
    contact VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    membership_level ENUM('Regular','Silver','Gold') NOT NULL DEFAULT 'Regular'
);

-- RESERVATION TABLE
CREATE TABLE reservation (
    reservation_id VARCHAR(10) PRIMARY KEY,
    equipment_id VARCHAR(10) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('Active','Cancelled','Converted') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- RENTAL TABLE
CREATE TABLE rental (
    rental_id VARCHAR(10) PRIMARY KEY,
    equipment_id VARCHAR(10) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    branch_id VARCHAR(10) NOT NULL,
    reservation_id VARCHAR(10),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    actual_return_date DATE,
    rental_amount DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    membership_discount DECIMAL(10,2) DEFAULT 0.00,
    long_rental_discount DECIMAL(10,2) DEFAULT 0.00,
    late_fee DECIMAL(10,2) DEFAULT 0.00,
    damage_charge DECIMAL(10,2) DEFAULT 0.00,
    damage_description TEXT,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Paid','Partially Paid','Unpaid') NOT NULL DEFAULT 'Unpaid',
    rental_status ENUM('Active','Returned','Overdue','Cancelled') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id),
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)
);

-- MEMBERSHIP CONFIG TABLE
CREATE TABLE membership_config (
    membership_level ENUM('Regular','Silver','Gold') PRIMARY KEY,
    discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00
);


-- =================== SAMPLE DATA ===================

-- Branches
INSERT INTO branch VALUES
('B001', 'Panadura Branch', 'No 12, Galle Road, Panadura',          '0382-223344'),
('B002', 'Galle Branch',    'No 45, Matara Road, Galle',            '0912-234455'),
('B003', 'Colombo Branch',  'No 78, Duplication Road, Colombo 03',  '0112-345566');

-- Categories
INSERT INTO category VALUES
('C001', 'Camera',   'Professional cameras for photography and videography', 1.00, 1.20, 500.00,  TRUE),
('C002', 'Drone',    'Aerial photography drones',                            1.80, 1.30, 1000.00, TRUE),
('C003', 'Lens',     'Camera lenses and accessories',                        0.80, 1.10, 300.00,  TRUE),
('C004', 'Lighting', 'Professional lighting kits',                           0.90, 1.10, 400.00,  TRUE),
('C005', 'Audio',    'Professional audio equipment',                         0.85, 1.10, 350.00,  TRUE);

-- Equipment
-- B001 - Panadura
INSERT INTO equipment VALUES
('E001', 'C001', 'B001', 'Sony',     'Alpha A7 III',    2022, 5000.00, 25000.00, 'Available'),
('E002', 'C001', 'B001', 'Canon',    'EOS R5',          2023, 6000.00, 30000.00, 'Available'),
('E003', 'C002', 'B001', 'DJI',      'Mavic 3 Pro',     2023, 8000.00, 40000.00, 'Available'),
('E004', 'C003', 'B001', 'Sony',     'FE 24-70mm f2.8', 2021, 2500.00, 12000.00, 'Available'),
('E005', 'C004', 'B001', 'Godox',    'SL-60W LED',      2022, 1500.00,  7000.00, 'Available'),
('E016', 'C001', 'B001', 'Fujifilm', 'X-T5',            2023, 4000.00, 20000.00, 'Available'), -- R001 Returned, so Available
('E017', 'C005', 'B001', 'Zoom',     'H6 Recorder',     2022, 1200.00,  6000.00, 'Available'),
('E020', 'C002', 'B001', 'Autel',    'EVO II Pro',      2022, 7000.00, 35000.00, 'Available');

-- B002 - Galle
INSERT INTO equipment VALUES
('E006', 'C001', 'B002', 'Nikon',     'Z6 II',         2022, 4500.00, 22000.00, 'Rented'),        -- R002 Overdue
('E007', 'C002', 'B002', 'DJI',       'Air 3',         2023, 6000.00, 30000.00, 'Rented'),        -- R003 Overdue
('E008', 'C003', 'B002', 'Canon',     'RF 50mm f1.2',  2022, 3000.00, 15000.00, 'Available'),
('E009', 'C004', 'B002', 'Aputure',   '120D II',       2021, 2000.00, 10000.00, 'Available'),
('E010', 'C005', 'B002', 'Rode',      'NTG5 Shotgun',  2022, 1800.00,  9000.00, 'Available'),
('E018', 'C004', 'B002', 'Neewer',    'RGB Led Panel', 2023,  800.00,  4000.00, 'Under Maintenance'),
('E021', 'C001', 'B002', 'Panasonic', 'Lumix S5 II',   2023, 4800.00, 24000.00, 'Available');

-- B003 - Colombo
INSERT INTO equipment VALUES
('E011', 'C001', 'B003', 'Sony',          'Alpha A1',       2023,  9000.00, 45000.00, 'Rented'),  -- R004 Active
('E012', 'C002', 'B003', 'DJI',           'Inspire 2',      2022, 12000.00, 60000.00, 'Available'),
('E013', 'C003', 'B003', 'Sigma',         '85mm f1.4 Art',  2021,  2800.00, 14000.00, 'Rented'),  -- R005 Active
('E014', 'C004', 'B003', 'Godox',         'AD600Pro',       2022,  3500.00, 17000.00, 'Available'),
('E015', 'C005', 'B003', 'Sennheiser',    'MKH416',         2021,  2200.00, 11000.00, 'Available'),
('E019', 'C003', 'B003', 'Tamron',        '17-28mm f2.8',   2022,  2000.00, 10000.00, 'Available'),
('E022', 'C005', 'B003', 'Audio Technica','AT4053b',        2021,  1600.00,  8000.00, 'Available');

-- Users (plain text passwords — matches current AuthService .equals() check)
INSERT INTO user VALUES
('U001', 'admin',    'admin123',   'System Admin',      'Admin',          NULL),
('U002', 'manager1', 'manager123', 'Kamal Perera',      'Branch Manager', 'B001'),
('U003', 'manager2', 'manager123', 'Nimal Silva',       'Branch Manager', 'B002'),
('U004', 'manager3', 'manager123', 'Sunil Fernando',    'Branch Manager', 'B003'),
('U005', 'staff1',   'staff123',   'Amara Dissanayake', 'Staff',          'B001'),
('U006', 'staff2',   'staff123',   'Priya Jayawardena', 'Staff',          'B002'),
('U007', 'staff3',   'staff123',   'Roshan Bandara',    'Staff',          'B003');

-- Customers
INSERT INTO customer VALUES
('CU001', 'Ashan Perera',         '199012345678', '0771234567', 'ashan@email.com',    'No 5, Colombo',    'Gold'),
('CU002', 'Malini Silva',         '198765432109', '0712345678', 'malini@email.com',   'No 12, Galle',     'Silver'),
('CU003', 'Ruwan Fernando',       '200112345670', '0761234567', 'ruwan@email.com',    'No 23, Panadura',  'Regular'),
('CU004', 'Dilini Jayasuriya',    '199234567890', '0751234567', 'dilini@email.com',   'No 34, Matara',    'Silver'),
('CU005', 'Kasun Bandara',        '198890123456', '0741234567', 'kasun@email.com',    'No 45, Kandy',     'Gold'),
('CU006', 'Nimali Wickrama',      '200023456789', '0731234567', 'nimali@email.com',   'No 56, Negombo',   'Regular'),
('CU007', 'Chamara Gunawardena',  '199567890123', '0721234567', 'chamara@email.com',  'No 67, Kurunegala','Regular'),
('CU008', 'Sachini Rajapaksa',    '200145678901', '0711234567', 'sachini@email.com',  'No 78, Gampaha',   'Silver'),
('CU009', 'Lahiru Madushanka',    '199789012345', '0701234567', 'lahiru@email.com',   'No 89, Kalutara',  'Gold'),
('CU010', 'Thilini Senanayake',   '200234567890', '0761234568', 'thilini@email.com',  'No 90, Ratnapura', 'Regular');

-- Membership Config
INSERT INTO membership_config VALUES
('Regular', 0.00),
('Silver',  5.00),
('Gold',   10.00);

-- Sample Rentals
-- R001: Returned, with damage charge (E016 → Available)
INSERT INTO rental VALUES
('R001', 'E016', 'CU001', 'B001', NULL,
 '2026-04-01', '2026-04-10', '2026-04-11',
 45000.00, 20000.00, 4500.00, 0.00, 0.00, 2000.00,
 'Minor scratch on body', 42500.00, 'Paid', 'Returned', NOW());

-- R002: Overdue, unpaid (E006 → Rented)
INSERT INTO rental VALUES
('R002', 'E006', 'CU002', 'B002', NULL,
 '2026-04-15', '2026-04-20', NULL,
 22500.00, 22000.00, 1125.00, 0.00, 0.00, 0.00,
 NULL, 21375.00, 'Unpaid', 'Overdue', NOW());

-- R003: Overdue, paid (E007 → Rented)
INSERT INTO rental VALUES
('R003', 'E007', 'CU005', 'B002', NULL,
 '2026-04-20', '2026-04-25', NULL,
 36000.00, 30000.00, 3600.00, 0.00, 0.00, 0.00,
 NULL, 32400.00, 'Paid', 'Overdue', NOW());

-- R004: Active, long rental (E011 → Rented)
INSERT INTO rental VALUES
('R004', 'E011', 'CU009', 'B003', NULL,
 '2026-05-01', '2026-05-07', NULL,
 63000.00, 45000.00, 6300.00, 6300.00, 0.00, 0.00,
 NULL, 50400.00, 'Unpaid', 'Active', NOW());

-- R005: Active, Silver discount (E013 → Rented)
INSERT INTO rental VALUES
('R005', 'E013', 'CU004', 'B003', NULL,
 '2026-05-05', '2026-05-08', NULL,
 8400.00, 14000.00, 420.00, 0.00, 0.00, 0.00,
 NULL, 7980.00, 'Paid', 'Active', NOW());

-- R006: Returned, Gold discount + long rental (E001 → Available)
INSERT INTO rental VALUES
('R006', 'E001', 'CU005', 'B001', NULL,
 '2026-03-01', '2026-03-10', '2026-03-10',
 50000.00, 25000.00, 5000.00, 5000.00, 0.00, 0.00,
 NULL, 40000.00, 'Paid', 'Returned', NOW());

-- Sample Reservation (Active)
INSERT INTO reservation VALUES
('RS001', 'E003', 'CU003', 'B001',
 '2026-05-15', '2026-05-18',
 'Active', NOW());

-- Update E003 status to Reserved (matches RS001)
UPDATE equipment SET status = 'Reserved' WHERE equipment_id = 'E003';
