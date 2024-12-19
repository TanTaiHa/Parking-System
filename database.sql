create database vehicle_parking;
use vehicle_parking;
CREATE TABLE IF NOT EXISTS vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    vehicle_number VARCHAR(255) NOT NULL,
    mobile VARCHAR(20),
    gate_index INT,
    slot_index INT,
    entry_time DATETIME,
    exit_time DATETIME DEFAULT NULL,
    duration INT,
    taken_vehicle BOOLEAN DEFAULT TRUE
);