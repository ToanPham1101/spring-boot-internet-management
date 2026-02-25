-- =============================================
-- Câu 3.1: Top 3 users with highest total spending
-- Only counts DONE orders (order_status = 2)
-- total_spent = sum of (quantity * price) per order minus order discount
-- =============================================
SELECT
    sub.user_id,
    SUM(sub.order_total) AS total_spent
FROM (
    SELECT
        o.id AS order_id,
        o.user_id,
        COALESCE(SUM(oi.quantity * oi.price), 0) - o.discount AS order_total
    FROM orders o
    LEFT JOIN order_item oi ON o.id = oi.order_id
    WHERE o.order_status = 2
    GROUP BY o.id, o.user_id, o.discount
) sub
GROUP BY sub.user_id
ORDER BY total_spent DESC
LIMIT 3;


-- =============================================
-- Câu 3.2: Top 5 items with highest revenue in Q4 2025
-- Only counts DONE orders (order_status = 2)
-- Q4 2025 = October 1, 2025 to December 31, 2025
-- =============================================
SELECT
    oi.item_id,
    i.name AS item_name,
    SUM(oi.quantity) AS total_quantity,
    SUM(oi.quantity * oi.price) AS total_revenue
FROM order_item oi
JOIN orders o ON oi.order_id = o.id
JOIN items i ON oi.item_id = i.id
WHERE o.order_status = 2
  AND o.order_date >= '2025-10-01'
  AND o.order_date <= '2025-12-31'
GROUP BY oi.item_id, i.name
ORDER BY total_revenue DESC
LIMIT 5;

