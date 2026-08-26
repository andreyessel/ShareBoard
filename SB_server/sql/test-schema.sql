DROP DATABASE IF EXISTS shareboard_test;
CREATE DATABASE shareboard_test;
USE shareboard_test;

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




USE shareboard_test;

DROP PROCEDURE IF EXISTS set_known_good_state;

DELIMITER //

CREATE PROCEDURE set_known_good_state()
BEGIN
                                                                                                                                                                            -- delete children before parents, same FK-order rule as any delete you've done before
    DELETE FROM match_line;
    DELETE FROM listing;
    DELETE FROM category;
    DELETE FROM user;

                                                                                                                                                                                             -- reset auto-increment so IDs are predictable every time this runs
    ALTER TABLE match_line AUTO_INCREMENT = 1;
    ALTER TABLE listing AUTO_INCREMENT = 1;
    ALTER TABLE category AUTO_INCREMENT = 1;
    ALTER TABLE user AUTO_INCREMENT = 1;

                                                                                                                                                                                             -- a small, known dataset — enough to exercise your repository methods, nothing more
    INSERT INTO user (id, username, password_hash, is_organization) VALUES
        (1, 'test_org', 'hash1', TRUE),
        (2, 'test_donor', 'hash2', FALSE);

    INSERT INTO category (id, name) VALUES
        (1, 'Blankets'),
        (2, 'Canned Food');

    INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status) VALUES
        (1, 1, 1, 'NEED', 10, 10, 'HIGH', '2026-09-01', 'OPEN');

    INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status) VALUES
        (2, 2, 1, 'SURPLUS', 5, 5, '2026-09-10', 'OPEN');
END //

DELIMITER ;




SELECT DATABASE();


SHOW PROCEDURE STATUS WHERE Db = 'shareboard_test';


CALL set_known_good_state();

SELECT * FROM listing;