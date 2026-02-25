-- ===========================================================================
-- V2: Thêm indexes để tối ưu truy vấn
-- ===========================================================================

-- Tìm user theo username
CREATE INDEX idx_users_username ON users (username);

-- Tìm giao dịch theo user
CREATE INDEX idx_transactions_user_id ON user_balance_transactions (user_id);

-- Tìm phiên theo user + status
CREATE INDEX idx_sessions_user_status ON sessions (user_id, status);

-- Tìm giỏ hàng theo user
CREATE INDEX idx_cart_item_user_id ON cart_item (user_id);

-- Tìm đơn hàng theo user + status
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (order_status);

-- Tìm chi tiết đơn hàng theo order
CREATE INDEX idx_order_item_order_id ON order_item (order_id);

-- Tìm item theo loại
CREATE INDEX idx_items_type ON items (item_type);

