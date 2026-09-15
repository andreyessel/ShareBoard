DROP DATABASE IF EXISTS shareboard;
CREATE DATABASE shareboard;
USE shareboard;


CREATE TABLE user (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(30) NOT NULL UNIQUE,
            password_hash VARCHAR(255) NOT NULL,
            is_organization BOOLEAN NOT NULL DEFAULT FALSE,
            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
             id INT AUTO_INCREMENT PRIMARY KEY,
             name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE listing (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            category_id INT NOT NULL,
            type VARCHAR(10) NOT NULL,
            quantity INT NOT NULL,
            quantity_remaining INT NOT NULL,
            urgency VARCHAR(10) NULL,
            needed_by DATE NULL,
            available_until DATE NULL,
            status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
            anonymous BOOLEAN NOT NULL DEFAULT FALSE,
            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id),
            FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE match_line (
            id INT AUTO_INCREMENT PRIMARY KEY,
            need_listing_id INT NOT NULL,
            surplus_listing_id INT NOT NULL,
            quantity_matched INT NOT NULL,
            matched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (need_listing_id) REFERENCES listing(id),
            FOREIGN KEY (surplus_listing_id) REFERENCES listing(id)
);