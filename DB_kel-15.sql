CREATE DATABASE event_organizer_db;

USE event_organizer_db;

-- Tabel User
CREATE TABLE USER (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel Events
CREATE TABLE EVENTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL, -- Nama event
    TYPE VARCHAR(50) NOT NULL,
    description TEXT, -- Deskripsi tambahan
    category VARCHAR(50), -- Kategori event
    DATE DATE NOT NULL,
    location VARCHAR(100) NOT NULL
);

-- Tabel Clients
CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    event_id INT,
    FOREIGN KEY (event_id) REFERENCES EVENTS(id) ON DELETE SET NULL
);

-- Tabel Schedules
CREATE TABLE schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    STATUS VARCHAR(20) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES EVENTS(id) ON DELETE CASCADE
);