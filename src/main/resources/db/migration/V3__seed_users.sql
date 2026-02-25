-- ===========================================================================
-- V3: Dữ liệu seed — Gói cước & Người dùng
-- ===========================================================================

-- Gói cước
INSERT INTO categories(id, name, price_per_hour)
VALUES (1, 'NORMAL', 10000),
       (2, 'VIP', 15000),
       (3, 'VVIP', 20000);

-- Người dùng
INSERT INTO users(id, username, full_name, password, balance, category_id)
VALUES (1, 'hblab', 'HB Lab User', '123456', 500000, 1),
       (2, 'user_1', 'Nguyen Van A', '123456', 300000, 1),
       (3, 'user_2', 'Tran Thi B', '123456', 450000, 2),
       (4, 'user_3', 'Le Van C', '123456', 200000, 1),
       (5, 'user_4', 'Pham Thi D', '123456', 600000, 3),
       (6, 'user_5', 'Hoang Van E', '123456', 350000, 2),
       (7, 'user_6', 'Do Thi F', '123456', 250000, 1),
       (8, 'user_7', 'Vu Van G', '123456', 400000, 2),
       (9, 'user_8', 'Bui Thi H', '123456', 150000, 1),
       (10, 'user_9', 'Dang Van I', '123456', 550000, 3),
       (11, 'user_10', 'Ngo Thi K', '123456', 280000, 1),
       (12, 'user_11', 'Trinh Van L', '123456', 320000, 2),
       (13, 'user_12', 'Mai Thi M', '123456', 180000, 1),
       (14, 'user_13', 'Cao Van N', '123456', 420000, 2),
       (15, 'user_14', 'Ly Thi O', '123456', 700000, 3),
       (16, 'user_15', 'Duong Van P', '123456', 160000, 1),
       (17, 'user_16', 'Tong Thi Q', '123456', 380000, 2),
       (18, 'user_17', 'Lam Van R', '123456', 220000, 1),
       (19, 'user_18', 'Dinh Thi S', '123456', 480000, 3),
       (20, 'user_19', 'Ha Van T', '123456', 130000, 1),
       (21, 'user_20', 'Truong Thi U', '123456', 360000, 2);

-- Lịch sử nạp tiền ban đầu
INSERT INTO user_balance_transactions(user_id, amount, type, description)
VALUES (1, 500000, 1, 'Initial deposit'),
       (2, 300000, 1, 'Initial deposit'),
       (3, 450000, 1, 'Initial deposit'),
       (4, 200000, 1, 'Initial deposit'),
       (5, 600000, 1, 'Initial deposit'),
       (6, 350000, 1, 'Initial deposit'),
       (7, 250000, 1, 'Initial deposit'),
       (8, 400000, 1, 'Initial deposit'),
       (9, 150000, 1, 'Initial deposit'),
       (10, 550000, 1, 'Initial deposit'),
       (11, 280000, 1, 'Initial deposit'),
       (12, 320000, 1, 'Initial deposit'),
       (13, 180000, 1, 'Initial deposit'),
       (14, 420000, 1, 'Initial deposit'),
       (15, 700000, 1, 'Initial deposit'),
       (16, 160000, 1, 'Initial deposit'),
       (17, 380000, 1, 'Initial deposit'),
       (18, 220000, 1, 'Initial deposit'),
       (19, 480000, 1, 'Initial deposit'),
       (20, 130000, 1, 'Initial deposit'),
       (21, 360000, 1, 'Initial deposit');

-- Phiên sử dụng mẫu
INSERT INTO sessions(user_id, category_id, start_time, end_time, price_per_hour, status)
VALUES (1, 1, '2025-10-01 08:00:00', '2025-10-01 11:00:00', 10000, 2),
       (3, 2, '2025-10-02 09:00:00', '2025-10-02 12:30:00', 15000, 2),
       (5, 3, '2025-10-03 10:00:00', NULL, 20000, 1);

