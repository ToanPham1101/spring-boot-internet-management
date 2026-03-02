-- ===========================================================================
-- V6: Thêm cột role cho bảng users — phân quyền ADMIN / USER
-- ===========================================================================

ALTER TABLE users ADD COLUMN role VARCHAR(10) NOT NULL DEFAULT 'USER';

-- Gán quyền ADMIN cho user ToanPDT (id=1)
UPDATE users SET role = 'ADMIN' WHERE id = 1;

