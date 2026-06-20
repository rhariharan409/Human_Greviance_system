-- SQL Script to set up database tables
CREATE DATABASE IF NOT EXISTS citizen_complaints;
USE citizen_complaints;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    phone_number VARCHAR(15),
    email VARCHAR(100),
    address VARCHAR(255),
    department VARCHAR(50),
    status_or_level VARCHAR(20)
);

-- Create Complaints table
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    ward VARCHAR(50) NOT NULL,
    affected_people INT NOT NULL,
    severity INT NOT NULL,
    priority_score INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    citizen_username VARCHAR(50) NOT NULL,
    assigned_employee_username VARCHAR(50) NOT NULL,
    days_pending INT NOT NULL
);

-- Pre-populate default accounts
INSERT IGNORE INTO users (username, password_hash, full_name, role, phone_number, email, address, department, status_or_level) VALUES
('admin', 'YWRtaW4xMjM=', 'System Administrator', 'ADMIN', '', '', '', '', '1'),
('emp1', 'ZW1wMTIz=', 'John Doe (Water Dept)', 'EMPLOYEE', '', '', '', 'WATER', 'ACTIVE'),
('emp2', 'ZW1wMTIz=', 'Jane Smith (Sanitation Dept)', 'EMPLOYEE', '', '', '', 'SANITATION', 'ACTIVE'),
('citizen', 'Y2l0MTIz=', 'Alice Brown', 'CITIZEN', '9876543210', 'alice@gmail.com', '12 Ward Road', '', '');
