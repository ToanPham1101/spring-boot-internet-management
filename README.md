# Internet Shop Service - Item API

## Giới thiệu
- Java 17
- Gradle 8.5
- Spring Boot 3.1.0 (Web, JPA)
- Lombok
- Database H2
  - gồm các bảng items, orders, order_item, cart_item, users, user_balance_transactions, categories, sessions
  - bảng và dữ liệu được tự động tạo khi start app (xem thêm [schema.sql](src/main/resources/schema.sql) và [data.sql](src/main/resources/data.sql))
  - Cách vào database
    - username/password là sa/123456
    - dùng plugin database của IntelliJ IDEA, JDBC URL `jdbc:h2:tcp://localhost:9092/mem:test`
    - mở http://localhost:8080/h2, JDBC URL `jdbc:h2:mem:test`
- Swagger
  - mở http://localhost:8080 để vào màn hình Swagger, xem và test api

---

## Relational Database Structure - Internet Shop Service

### Business Rules

| Category | Price         | Description              |
|----------|---------------|--------------------------|
| NORMAL   | 10.000 VNĐ/h | Standard internet access |
| VIP      | 15.000 VNĐ/h | Priority internet access |
| VVIP     | 20.000 VNĐ/h | Premium internet access  |

**Features:** Create account, Add money, Get time remaining, Order food & drink items (deducted from remaining balance)

---

### ER Diagram (Text-based)

```
┌─────────────────────┐       ┌──────────────────────────────┐
│       users         │       │        categories            │
├─────────────────────┤       ├──────────────────────────────┤
│ PK  id         INT  │       │ PK  id            INT        │
│     username   VCR  │       │     name          VCR        │
│     full_name  VCR  │       │     price_per_hour INT       │
│     password   VCR  │       │                              │
│     balance    INT  │       │  NORMAL  = 10000 VND/h       │
│ FK  category_id INT ├──────>│  VIP     = 15000 VND/h       │
│     created_at TS   │       │  VVIP    = 20000 VND/h       │
└────────┬────────────┘       └──────────────────────────────┘
         │
         │ 1:N
         │
         ├───────────────────────────────────────┐
         │                                       │
         ▼                                       ▼
┌─────────────────────────────┐   ┌──────────────────────────────┐
│  user_balance_transactions  │   │         sessions             │
├─────────────────────────────┤   ├──────────────────────────────┤
│ PK  id              INT     │   │ PK  id             INT       │
│ FK  user_id         VCR     │   │ FK  user_id        VCR       │
│     amount          INT     │   │ FK  category_id    INT       │
│     type            INT     │   │     start_time     TIMESTAMP │
│       1=DEPOSIT             │   │     end_time       TIMESTAMP │
│       2=ORDER_PAYMENT       │   │     price_per_hour INT       │
│     description     VCR     │   │     status         INT       │
│     created_at      TS      │   │       1=ACTIVE               │
│                             │   │       2=EXPIRED              │
└─────────────────────────────┘   │       3=CANCELLED            │
                                  └──────────────────────────────┘
         │
         │
         ▼
┌─────────────────────┐        ┌─────────────────────────────┐
│      orders         │        │        items                │
├─────────────────────┤        ├─────────────────────────────┤
│ PK  id         INT  │        │ PK  id           INT        │
│ FK  user_id    VCR  │        │     name         VCR        │
│     discount   INT  │        │     price        INT        │
│     order_status INT│        │     item_type    INT        │
│       1=NEW         │        │       1=FOOD                │
│       2=DONE        │        │       2=DRINK               │
│       3=CANCEL      │        └──────────┬──────────────────┘
│     order_date DATE │                   │
│     total_amount INT│                   │
└────────┬────────────┘                   │
         │                                │
         │ 1:N                            │
         ▼                                │
┌──────────────────────────┐              │
│      order_item          │              │
├──────────────────────────┤              │
│ PK,FK  order_id    INT   ├──────────────┘
│ PK,FK  item_id     INT   │
│        quantity    INT   │
│        price       INT   │
└──────────────────────────┘

┌─────────────────────┐
│      cart_item      │
├─────────────────────┤
│ PK,FK  user_id  VCR │──────> users.id
│ PK,FK  item_id  INT │──────> items.id
│        quantity INT │
└─────────────────────┘
```

---

### Table Details

#### 1. `categories` — Internet service categories
| Column         | Type         | Constraint  | Description               |
|----------------|--------------|-------------|---------------------------|
| id             | INTEGER      | PK, AUTO    | Category ID               |
| name           | VARCHAR(50)  | NOT NULL    | NORMAL / VIP / VVIP       |
| price_per_hour | INTEGER      | NOT NULL    | Price per hour (VNĐ)      |

**Sample Data:**

| id | name   | price_per_hour |
|----|--------|----------------|
| 1  | NORMAL | 10000          |
| 2  | VIP    | 15000          |
| 3  | VVIP   | 20000          |

---

#### 2. `users` — User accounts
| Column      | Type         | Constraint  | Description                  |
|-------------|--------------|-------------|------------------------------|
| id          | INTEGER      | PK, AUTO    | User ID                      |
| username    | VARCHAR(50)  | NOT NULL, UQ| Unique login name            |
| full_name   | VARCHAR(255) | NOT NULL    | Display name                 |
| password    | VARCHAR(255) | NOT NULL    | Hashed password              |
| balance     | INTEGER      | NOT NULL    | Current balance (VNĐ)        |
| category_id | INTEGER      | FK → categories.id | Subscription tier     |
| created_at  | TIMESTAMP    | NOT NULL    | Account creation time        |

---

#### 3. `user_balance_transactions` — Deposit / payment history
| Column      | Type         | Constraint  | Description                          |
|-------------|--------------|-------------|--------------------------------------|
| id          | INTEGER      | PK, AUTO    | Transaction ID                       |
| user_id     | INTEGER      | FK → users.id | User reference                    |
| amount      | INTEGER      | NOT NULL    | Amount in VNĐ (+ deposit, − payment)|
| type        | INTEGER      | NOT NULL    | 1=DEPOSIT, 2=ORDER_PAYMENT          |
| description | VARCHAR(255) |             | Note / reason                        |
| created_at  | TIMESTAMP    | NOT NULL    | Transaction time                     |

---

#### 4. `sessions` — Internet usage sessions
| Column         | Type         | Constraint  | Description                        |
|----------------|--------------|-------------|------------------------------------|
| id             | INTEGER      | PK, AUTO    | Session ID                         |
| user_id        | INTEGER      | FK → users.id | User reference                  |
| category_id    | INTEGER      | FK → categories.id | Category at session start   |
| start_time     | TIMESTAMP    | NOT NULL    | Session start                      |
| end_time       | TIMESTAMP    |             | Session end (NULL if active)       |
| price_per_hour | INTEGER      | NOT NULL    | Snapshot of rate at session start  |
| status         | INTEGER      | NOT NULL    | 1=ACTIVE, 2=EXPIRED, 3=CANCELLED  |

**Time Remaining Calculation:**
```
remaining_balance = users.balance
time_remaining_hours = remaining_balance / categories.price_per_hour

Example: User has 50,000 VNĐ with VIP (15,000/h)
  → Time remaining = 50,000 / 15,000 = 3.33 hours ≈ 3h 20m
```

---

#### 5. `items` — Food & drink menu
| Column    | Type         | Constraint | Description               |
|-----------|--------------|------------|---------------------------|
| id        | INTEGER      | PK, AUTO   | Item ID                   |
| name      | VARCHAR(255) | NOT NULL   | Item name                 |
| price     | INTEGER      | NOT NULL   | Price (VNĐ)              |
| item_type | INTEGER      |            | 1=FOOD, 2=DRINK           |

---

#### 6. `orders` — Food & drink orders (deducted from balance)
| Column       | Type         | Constraint  | Description                    |
|--------------|--------------|-------------|--------------------------------|
| id           | INTEGER      | PK, AUTO    | Order ID                       |
| user_id      | VARCHAR(16)  | FK → users.id | User who placed order       |
| discount     | INTEGER      | NOT NULL    | Discount amount (VNĐ)         |
| order_status | INTEGER      | NOT NULL    | 1=NEW, 2=DONE, 3=CANCEL       |
| order_date   | DATE         | NOT NULL    | Date of order                  |
| total_amount | INTEGER      |             | Total after discount           |

---

#### 7. `order_item` — Items in each order
| Column   | Type    | Constraint          | Description            |
|----------|---------|---------------------|------------------------|
| order_id | INTEGER | PK, FK → orders.id  | Order reference        |
| item_id  | INTEGER | PK, FK → items.id   | Item reference         |
| quantity | INTEGER | NOT NULL            | Number of items        |
| price    | INTEGER | NOT NULL            | Price at time of order |

---

#### 8. `cart_item` — Shopping cart (before ordering)
| Column   | Type        | Constraint          | Description           |
|----------|-------------|---------------------|-----------------------|
| user_id  | VARCHAR(16) | PK, FK → users.id   | User reference        |
| item_id  | INTEGER     | PK, FK → items.id   | Item reference        |
| quantity | INTEGER     | NOT NULL            | Quantity in cart       |

---

### Relationships Summary

```
categories (1) ────── (N) users
users      (1) ────── (N) user_balance_transactions
users      (1) ────── (N) sessions
users      (1) ────── (N) orders
users      (1) ────── (N) cart_item
orders     (1) ────── (N) order_item
items      (1) ────── (N) order_item
items      (1) ────── (N) cart_item
categories (1) ────── (N) sessions
```

---

### Business Flow

```
1. CREATE ACCOUNT
   └─> INSERT into users (choose category: NORMAL/VIP/VVIP)
       └─> balance = 0

2. ADD MONEY (Deposit)
   ├─> INSERT into user_balance_transactions (type=DEPOSIT)
   └─> UPDATE users SET balance = balance + amount

3. GET TIME REMAINING
   └─> time_remaining = users.balance / categories.price_per_hour
       Example:
         balance = 100,000 VNĐ, category = VIP (15,000/h)
         → time = 100,000 / 15,000 = 6h 40m

4. ORDER FOOD & DRINK (deducted from balance)
   ├─> Add items to cart_item
   ├─> POST /order → creates order + order_items
   ├─> total = SUM(quantity × price) − discount
   ├─> UPDATE users SET balance = balance − total
   ├─> INSERT into user_balance_transactions (type=ORDER_PAYMENT)
   └─> DELETE from cart_item (clear cart)

   Remaining time after order:
     new_balance = old_balance − order_total
     new_time = new_balance / price_per_hour
```

---

### SQL Examples

#### Get time remaining for a user
```sql
SELECT
    u.username,
    u.balance,
    c.name AS category,
    c.price_per_hour,
    u.balance / c.price_per_hour AS remaining_hours,
    (u.balance % c.price_per_hour) * 60 / c.price_per_hour AS remaining_minutes
FROM users u
JOIN categories c ON u.category_id = c.id
WHERE u.id = :userId;
```

#### Top 3 users by total spending (Câu 3.1)
```sql
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
```

#### Top 5 items by revenue in Q4 2025 (Câu 3.2)
```sql
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
```

---

## REST APIs

#### 1. `GET /item/search` — Search items
- `GET /item/search` → all items
- `GET /item/search?key=abc` → items with name containing "abc"
- `GET /item/search?key=123` → item id=123 + items with name containing "123"

#### 2. `GET /order/search` — Search orders
- `GET /order/search` → all orders
- `GET /order/search?userId=hblab` → orders by user
- `GET /order/search?orderStatus=NEW` → orders by status
- `GET /order/search?userId=hblab&orderStatus=CANCEL` → combined filter

#### 3. `GET /cart` — Get cart items
- `GET /cart?userId=hblab` → cart items with item id, name, price, quantity

#### 4. `POST /cart/quantity` — Update cart quantity
```json
{ "userId": "hblab", "itemId": 1, "quantity": 3 }
```
- New item → insert
- Existing item → update quantity
- quantity = 0 → remove from cart

#### 5. `POST /order` — Create order from cart
```json
{ "userId": "hblab", "discount": 5000 }
```
- Creates order with items from cart
- Clears the cart after order creation

