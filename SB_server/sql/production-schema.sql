-- ============================================================================
-- ShareBoard — full schema + seed data
-- Safe to run repeatedly: drops and rebuilds the database from scratch.
-- Run against your real `shareboard` database (not shareboard_test).
-- Dates assume you're running this around September 2026 — adjust the years
-- if you're running it later, so nothing accidentally lands in the past.
-- ============================================================================

DROP DATABASE IF EXISTS shareboard;
CREATE DATABASE shareboard;
USE shareboard;

-- ----------------------------------------------------------------------------
-- Schema
-- ----------------------------------------------------------------------------

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

-- ----------------------------------------------------------------------------
-- Categories
-- ----------------------------------------------------------------------------

INSERT INTO category (id, name) VALUES
                                    (1, 'Blankets'),
                                    (2, 'Canned Food'),
                                    (3, 'Baby Formula'),
                                    (4, 'Coats'),
                                    (5, 'School Supplies'),
                                    (6, 'Hygiene Kits');

-- ----------------------------------------------------------------------------
-- Users — a mix of organizations (for the verified-org badge) and
-- individuals. password_hash values here are placeholders — register fresh
-- accounts through the app's own sign-up flow to actually log in; these rows
-- exist to populate listings with realistic-looking posters, not to log in as.
-- ----------------------------------------------------------------------------

INSERT INTO user (id, username, password_hash, is_organization) VALUES
                                                                    (1, 'riverside_shelter', 'placeholder', TRUE),
                                                                    (2, 'corner_market', 'placeholder', TRUE),
                                                                    (3, 'hope_nursery', 'placeholder', TRUE),
                                                                    (4, 'downtown_pharmacy', 'placeholder', TRUE),
                                                                    (5, 'eastside_foodbank', 'placeholder', TRUE),
                                                                    (6, 'community_pantry', 'placeholder', TRUE),
                                                                    (7, 'j_rivera', 'placeholder', FALSE),
                                                                    (8, 'm_chen', 'placeholder', FALSE);

-- ----------------------------------------------------------------------------
-- Listings
-- ----------------------------------------------------------------------------

-- 1/2: fully matched pair — Blankets. Demonstrates the "Done" card state,
-- and listing 2 is posted anonymously (donor name hidden everywhere).
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (1, 1, 1, 'NEED', 50, 0, 'HIGH', '2026-09-20', 'MATCHED', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (2, 2, 1, 'SURPLUS', 50, 0, '2026-09-28', 'MATCHED', TRUE);
INSERT INTO match_line (id, need_listing_id, surplus_listing_id, quantity_matched, matched_at) VALUES
    (1, 1, 2, 50, '2026-09-14 10:15:00');

-- 3/4: partially matched pair — Baby Formula. Need still has 10 of 30 open;
-- the surplus that filled part of it is now fully consumed.
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (3, 3, 3, 'NEED', 30, 10, 'MEDIUM', '2026-09-25', 'PARTIALLY_MATCHED', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (4, 4, 3, 'SURPLUS', 20, 0, '2026-09-30', 'MATCHED', FALSE);
INSERT INTO match_line (id, need_listing_id, surplus_listing_id, quantity_matched, matched_at) VALUES
    (2, 3, 4, 20, '2026-09-15 14:40:00');

-- 5/6: deliberately NOT matched — the surplus expires (Sep 18) before the
-- need's deadline (Oct 10), so the deadline-crossing check correctly rejects
-- it. Good one to point at live during a demo.
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (5, 3, 3, 'NEED', 15, 15, 'LOW', '2026-10-10', 'OPEN', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (6, 4, 3, 'SURPLUS', 10, 10, '2026-09-18', 'OPEN', FALSE);

-- 7/8: an open, crossable pair left un-matched on purpose — post something
-- new against either side during a live demo to trigger a real match.
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (7, 5, 2, 'NEED', 200, 200, 'MEDIUM', '2026-10-05', 'OPEN', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (8, 6, 2, 'SURPLUS', 250, 250, '2026-10-20', 'OPEN', FALSE);

-- 9/10: Coats, both open, not necessarily crossed yet
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (9, 1, 4, 'NEED', 30, 30, 'LOW', '2026-10-15', 'OPEN', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (10, 2, 4, 'SURPLUS', 40, 40, '2026-11-01', 'OPEN', FALSE);

-- 11: withdrawn — demonstrates the Withdrawn filter/status
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (11, 1, 5, 'NEED', 25, 25, 'LOW', '2026-10-01', 'WITHDRAWN', FALSE);

-- 12: an anonymous surplus with nothing matched against it yet
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (12, 6, 6, 'SURPLUS', 25, 25, '2026-11-05', 'OPEN', TRUE);

-- 13/14: more open needs across urgencies/categories for filter testing
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (13, 3, 6, 'NEED', 15, 15, 'HIGH', '2026-09-22', 'OPEN', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (14, 1, 4, 'NEED', 10, 10, 'HIGH', '2026-09-19', 'OPEN', FALSE);

-- 15/16: a bit more variety for search/category filtering
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, available_until, status, anonymous) VALUES
    (15, 2, 1, 'SURPLUS', 15, 15, '2026-10-25', 'OPEN', FALSE);
INSERT INTO listing (id, user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status, anonymous) VALUES
    (16, 5, 2, 'NEED', 40, 40, 'HIGH', '2026-09-21', 'OPEN', FALSE);