-- ===========================================================================
-- V4: Dữ liệu seed — Menu đồ ăn & thức uống
-- ===========================================================================

-- FOOD (item_type = 1)
INSERT INTO items(id, name, price, item_type)
VALUES (1, 'Xúc xích CP 40g', 5000, 1),
       (2, 'Mỳ tôm ly Hảo Hảo', 10000, 1),
       (3, 'Mè xửng Huế', 18000, 1),
       (4, 'Bánh đậu xanh Bảo Đại', 12000, 1),
       (5, 'Bánh tráng sữa', 15000, 1),
       (6, 'Bánh pía Sóc Trăng', 25000, 1),
       (7, 'Coconut candy', 22000, 1),
       (8, 'Bánh tráng trộn', 15000, 1),
       (9, 'Bánh tét', 30000, 1),
       (10, 'Chuối nếp nướng', 20000, 1),
       (11, 'Bánh giò', 18000, 1),
       (12, 'Chả giò', 20000, 1),
       (13, 'Chả lụa', 25000, 1),
       (14, 'Bánh pate sô', 17000, 1),
       (15, 'Prawn crackers', 15000, 1),
       (16, 'Rice paper wrappers', 10000, 1),
       (17, 'Red Boat fish sauce', 40000, 1),
       (18, 'Three Ladies rice paper', 12000, 1),
       (19, 'Maggi seasoning (nắp đỏ)', 35000, 1),
       (20, 'Fried garlic topping', 25000, 1),
       (21, 'Bánh phồng tôm vị tỏi', 16000, 1),
       (22, 'Bánh phồng tôm vị ngọt', 16000, 1),
       (23, 'Cơm cháy chà bông cay', 21000, 1),
       (24, 'Cơm cháy chà bông mặn', 21000, 1),
       (25, 'Mè xửng vị truyền thống', 19000, 1),
       (26, 'Mè xửng vị sầu riêng', 20000, 1),
       (27, 'Bánh đậu xanh mini', 13000, 1),
       (28, 'Bánh đậu xanh hộp lớn', 30000, 1),
       (29, 'Bánh tráng sữa Bến Tre', 15000, 1),
       (30, 'Bánh tráng sữa mềm', 15000, 1);

-- DRINK (item_type = 2)
INSERT INTO items(id, name, price, item_type)
VALUES (31, 'Trà đá', 3000, 2),
       (32, 'Trà nóng', 5000, 2),
       (33, 'Cà phê đen đá', 15000, 2),
       (34, 'Cà phê đen nóng', 15000, 2),
       (35, 'Cà phê sữa đá', 18000, 2),
       (36, 'Cà phê sữa nóng', 18000, 2),
       (37, 'Bạc xỉu', 20000, 2),
       (38, 'Nước cam tươi', 25000, 2),
       (39, 'Nước ép dưa hấu', 20000, 2),
       (40, 'Sinh tố bơ', 25000, 2),
       (41, 'Sinh tố dâu', 25000, 2),
       (42, 'Trà đào', 22000, 2),
       (43, 'Trà vải', 22000, 2),
       (44, 'Trà chanh', 15000, 2),
       (45, 'Coca Cola', 12000, 2),
       (46, 'Pepsi', 12000, 2),
       (47, '7 Up', 12000, 2),
       (48, 'Sting', 10000, 2),
       (49, 'Red Bull', 15000, 2),
       (50, 'Nước suối', 5000, 2);

