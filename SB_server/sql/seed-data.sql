USE shareboard;

INSERT INTO category (name) VALUES
('Blankets'), ('Canned Food'), ('Baby Formula'), ('Coats'), ('School Supplies');

INSERT INTO user (username, password_hash, is_organization) VALUES
('riverside_shelter', 'hash1', TRUE),
('corner_market', 'hash2', TRUE),
('hope_nursery', 'hash3', TRUE),
('downtown_pharmacy', 'hash4', TRUE),
('j_rivera', 'hash5', FALSE),
('m_chen', 'hash6', FALSE);

-- Needs
INSERT INTO listing (user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, status) VALUES
(1, 1, 'NEED', 50, 50, 'HIGH', '2026-08-29', 'OPEN'),                 -- shelter needs blankets soon
(3, 3, 'NEED', 20, 20, 'MEDIUM', '2026-09-10', 'OPEN'),           -- nursery needs formula
(1, 5, 'NEED', 15, 15, 'LOW', '2026-09-20', 'OPEN');                   -- shelter needs school supplies, not urgent

-- Surplus
INSERT INTO listing (user_id, category_id, type, quantity, quantity_remaining, available_until, status) VALUES
(2, 1, 'SURPLUS', 30, 30, '2026-09-05', 'OPEN'),                     -- market's blankets — WILL cross the urgent need
(4, 3, 'SURPLUS', 10, 10, '2026-09-01', 'OPEN'),                        -- pharmacy's formula — expires BEFORE the need's deadline, so this one should NOT match (good demo of the deadline-crossing failure case)
(2, 5, 'SURPLUS', 20, 20, '2026-10-01', 'OPEN');                        -- market's school supplies — will cross the low-urgency need