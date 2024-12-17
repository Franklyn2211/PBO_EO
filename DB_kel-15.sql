CREATE DATABASE event_organizer_db;

USE event_organizer_db;

-- Tabel User
CREATE TABLE USER (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel Clients
CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES USER(id),
    FOREIGN KEY (updated_by) REFERENCES USER(id)
);

-- Tabel Events
CREATE TABLE EVENTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category ENUM('Besar', 'Kecil', 'Sedang') NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(100) NOT NULL,
    client_id INT,  -- Mengganti client_name dengan client_id sebagai referensi ke tabel clients
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES USER(id),
    FOREIGN KEY (updated_by) REFERENCES USER(id),
    FOREIGN KEY (client_id) REFERENCES clients(id)  -- Menambahkan foreign key yang menghubungkan dengan tabel clients
);

-- Tabel Schedules
CREATE TABLE schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    status ENUM('Selesai', 'Belum Selesai') NOT NULL,
    decoration_date DATE,  -- Tanggal jadwal dekorasi
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (event_id) REFERENCES EVENTS(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES USER(id),
    FOREIGN KEY (updated_by) REFERENCES USER(id)
);
