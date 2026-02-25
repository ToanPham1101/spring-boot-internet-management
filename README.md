# 🖥️ Internet Shop Management Service

Hệ thống quản lý quán internet — quản lý tài khoản người dùng, nạp tiền, tính thời gian sử dụng, gọi đồ ăn/thức uống trừ trực tiếp vào số dư.

---

## 📋 Mục lục

- [Tech Stack](#-tech-stack)
- [Kiến trúc dự án](#-kiến-trúc-dự-án)
- [Cách chạy](#-cách-chạy)
- [Mô tả nghiệp vụ](#-mô-tả-nghiệp-vụ-internet-shop)
- [Cấu trúc Database & Flyway](#-cấu-trúc-database--flyway)
- [ER Diagram](#-er-diagram)
- [Chi tiết các bảng](#-chi-tiết-các-bảng)
- [REST API](#-rest-api)
- [SQL mẫu](#-sql-mẫu)

---

## 🛠 Tech Stack

| Thành phần | Công nghệ | Phiên bản | Mô tả |
|------------|-----------|-----------|-------|
| **Ngôn ngữ** | Java | 17 | LTS, hỗ trợ records, sealed classes, pattern matching |
| **Framework** | Spring Boot | 3.1.0 | Framework chính — auto-configuration, embedded server |
| **Web** | Spring Web (MVC) | — | Xây dựng REST API, xử lý HTTP request/response |
| **ORM** | Spring Data JPA + Hibernate | — | Tương tác database qua Entity/Repository pattern |
| **Database** | H2 (in-memory) | — | DB nhúng dùng cho demo/test, có thể thay bằng PostgreSQL/MySQL |
| **DB Migration** | Flyway | — | Quản lý phiên bản database — tự động migrate khi khởi động |
| **Build tool** | Gradle | 8.5 | Quản lý dependency, build, test |
| **Code gen** | Lombok | 1.18.30 | Tự sinh getter/setter/constructor qua annotation (`@Data`, `@Getter`...) |
| **API docs** | SpringDoc OpenAPI (Swagger) | 2.1.0 | Tự tạo trang Swagger UI để xem và test API |

### Tại sao chọn các công nghệ này?

- **Spring Boot** — framework phổ biến nhất cho Java backend, cộng đồng lớn, hệ sinh thái phong phú
- **Flyway** — quản lý database migration theo version, đảm bảo schema nhất quán giữa các môi trường (dev/staging/prod). Mỗi lần thay đổi DB tạo 1 file migration mới, có thể rollback và audit
- **H2 in-memory** — DB nhúng, không cần cài đặt, phù hợp demo & test nhanh. Khi lên production chỉ cần đổi datasource URL sang PostgreSQL/MySQL
- **JPA/Hibernate** — ORM tiêu chuẩn, map Java object ↔ database table, giảm viết SQL thủ công
- **Lombok** — giảm boilerplate code (getter, setter, constructor), code gọn hơn
- **Swagger** — documentation tự động, có thể test API ngay trên trình duyệt

---

## 📁 Kiến trúc dự án

```
src/main/
├── java/item/
│   ├── ItemApplication.java              # Main class — điểm khởi chạy Spring Boot
│   ├── H2Config.java                     # Cấu hình H2 TCP server
│   │
│   ├── entity/                           # 🗃️ Entity — ánh xạ bảng database
│   │   ├── CategoryEntity.java           #   Bảng categories (NORMAL/VIP/VVIP)
│   │   ├── UserEntity.java               #   Bảng users (tài khoản người dùng)
│   │   ├── UserBalanceTransactionEntity.java  #   Bảng lịch sử giao dịch
│   │   ├── SessionEntity.java            #   Bảng phiên sử dụng internet
│   │   ├── ItemEntity.java               #   Bảng items (đồ ăn/thức uống)
│   │   ├── CartItemEntity.java           #   Bảng giỏ hàng
│   │   ├── CartItemId.java               #   Composite key (user_id, item_id)
│   │   ├── OrderEntity.java              #   Bảng đơn hàng
│   │   ├── OrderItemEntity.java          #   Bảng chi tiết đơn hàng
│   │   └── OrderItemId.java              #   Composite key (order_id, item_id)
│   │
│   ├── repository/                       # 🔍 Repository — truy vấn database
│   │   ├── CategoryRepository.java
│   │   ├── UserRepository.java
│   │   ├── UserBalanceTransactionRepository.java
│   │   ├── SessionRepository.java
│   │   ├── CartItemRepository.java
│   │   ├── OrderRepository.java
│   │   ├── OrderItemRepository.java
│   │   └── service/
│   │       └── ItemRepository.java
│   │
│   ├── service/                          # ⚙️ Service — xử lý logic nghiệp vụ
│   │   ├── UserService.java              #   Tạo tài khoản, nạp tiền, đổi gói
│   │   ├── SessionService.java           #   Bắt đầu/kết thúc phiên, tính thời gian còn lại
│   │   ├── SearchItemService.java        #   Tìm kiếm món ăn/thức uống
│   │   ├── CartService.java              #   Quản lý giỏ hàng
│   │   └── OrderService.java             #   Tạo đơn hàng, trừ tiền
│   │
│   ├── controller/                       # 🌐 Controller — REST API endpoints
│   │   ├── UserController.java           #   /user/*
│   │   ├── SessionController.java        #   /session/*
│   │   ├── ItemController.java           #   /item/*
│   │   ├── CartController.java           #   /cart/*
│   │   ├── OrderController.java          #   /order/*
│   │   └── GlobalExceptionHandler.java   #   Xử lý lỗi toàn cục
│   │
│   └── model/                            # 📦 DTO — dữ liệu truyền giữa client ↔ server
│       ├── CreateUserCommand.java
│       ├── DepositCommand.java
│       ├── CreateOrderCommand.java
│       ├── UpdateCartQuantityCommand.java
│       ├── GetCartQuery.java / GetCartResult.java
│       ├── SearchItemsQuery.java / SearchItemsResult.java
│       ├── SearchOrdersQuery.java / SearchOrdersResult.java
│       ├── UserResult.java / SessionResult.java
│       ├── OrderStatus.java              #   Enum: NEW(1), DONE(2), CANCEL(3)
│       ├── SessionStatus.java            #   Enum: ACTIVE(1), EXPIRED(2), CANCELLED(3)
│       ├── TransactionType.java          #   Enum: DEPOSIT(1), ORDER_PAYMENT(2), SESSION_PAYMENT(3)
│       └── ItemType.java                 #   Enum: FOOD(1), DRINK(2)
│
└── resources/
    ├── application.properties            # Cấu hình app (datasource, flyway, jpa, swagger)
    └── db/
        └── migration/                    # 📂 Flyway migration scripts
            ├── V1__create_schema.sql     #   Tạo 8 bảng + foreign keys
            ├── V2__create_indexes.sql    #   Thêm indexes tối ưu truy vấn
            ├── V3__seed_users.sql        #   Seed dữ liệu: gói cước, users, giao dịch, phiên
            ├── V4__seed_items.sql        #   Seed dữ liệu: menu đồ ăn & thức uống
            └── V5__seed_orders.sql       #   Seed dữ liệu: giỏ hàng, đơn hàng
```

**Luồng dữ liệu:** `Controller` → `Service` → `Repository` → `Database`

---

## 🚀 Cách chạy

```bash
# Clone project
git clone https://github.com/ToanPham1101/spring-boot-item-api.git
cd spring-boot-item-api

# Chạy ứng dụng
./gradlew bootRun
```

Sau khi khởi động:
| Tài nguyên | URL |
|-----------|-----|
| **Swagger UI** (xem & test API) | http://localhost:8080 |
| **H2 Console** (xem database) | http://localhost:8080/h2 |
| H2 JDBC URL | `jdbc:h2:mem:test` |
| H2 Username / Password | `sa` / `123456` |

> 💡 Khi app khởi động, **Flyway** tự động chạy các file migration theo thứ tự phiên bản (V1 → V2 → V3 → V4 → V5). Database được tạo và seed dữ liệu mẫu tự động.

---

## 💼 Mô tả nghiệp vụ Internet Shop

### Tổng quan

Quán internet cung cấp dịch vụ truy cập internet theo giờ với 3 gói cước, đồng thời bán đồ ăn/thức uống cho khách hàng. Toàn bộ chi phí được quản lý qua **số dư tài khoản** (balance).

### 3 gói cước

| Gói | Giá/giờ | Đối tượng |
|-----|---------|-----------|
| 🟢 **NORMAL** | 10.000 VNĐ/h | Khách thường, máy khu vực chung |
| 🟡 **VIP** | 15.000 VNĐ/h | Máy cấu hình cao, ghế thoải mái |
| 🔴 **VVIP** | 20.000 VNĐ/h | Phòng riêng, máy cao cấp nhất |

### Các nghiệp vụ chính

#### 1️⃣ Tạo tài khoản (`POST /user`)
- Khách hàng đăng ký tài khoản với username, họ tên, mật khẩu
- Chọn gói cước: `NORMAL`, `VIP`, hoặc `VVIP`
- Số dư ban đầu = **0 VNĐ**

#### 2️⃣ Nạp tiền (`POST /user/deposit`)
- Khách nạp tiền vào tài khoản (tại quầy thu ngân)
- Số dư tăng lên, ghi nhận lịch sử giao dịch (type = `DEPOSIT`)

> **Ví dụ:** Nạp 100.000 VNĐ → balance: 0 → 100.000

#### 3️⃣ Xem thời gian còn lại (`GET /session/time-remaining/{userId}`)
- Thời gian = `số_dư / giá_mỗi_giờ`
- Nếu đang có phiên đang chạy → trừ thêm chi phí đã dùng

> **Ví dụ:** Gói VIP (15.000/h), số dư 100.000 VNĐ
> → Thời gian còn = 100.000 ÷ 15.000 = **6 giờ 40 phút**

#### 4️⃣ Bắt đầu phiên sử dụng (`POST /session/start/{userId}`)
- Bắt đầu tính giờ, ghi nhận thời điểm `start_time`
- Mỗi user chỉ có **1 phiên active** tại một thời điểm
- Yêu cầu: số dư phải > 0

#### 5️⃣ Kết thúc phiên (`POST /session/end/{userId}`)
- Ghi nhận `end_time`, tính chi phí theo thời gian thực tế
- Trừ tiền khỏi số dư, ghi nhận giao dịch (type = `SESSION_PAYMENT`)

> **Ví dụ:** Dùng 2 giờ 30 phút, gói NORMAL (10.000/h)
> → Chi phí = 150 phút × 10.000 / 60 = **25.000 VNĐ**

#### 6️⃣ Gọi đồ ăn / thức uống
Menu gồm 2 loại:
- 🍔 **FOOD** — Xúc xích, bánh mì, mỳ tôm...
- 🥤 **DRINK** — Cà phê, nước cam, trà đá...

**Quy trình đặt đồ:**
```
Bước 1: Xem menu          →  GET  /item/search
                              GET  /item/food      (chỉ đồ ăn)
                              GET  /item/drink     (chỉ thức uống)

Bước 2: Thêm vào giỏ hàng →  POST /cart/quantity
                              { "userId": 1, "itemId": 33, "quantity": 2 }

Bước 3: Xem giỏ hàng      →  GET  /cart?userId=1

Bước 4: Đặt hàng          →  POST /order
                              { "userId": 1, "discount": 5000 }
```

Khi đặt hàng:
- Tính `tổng tiền = Σ(số_lượng × giá) − giảm_giá`
- Kiểm tra số dư đủ không → nếu thiếu thì báo lỗi
- **Trừ tiền** khỏi số dư → ghi nhận giao dịch (type = `ORDER_PAYMENT`)
- Xóa giỏ hàng

> **Ví dụ:** Gọi 2 ly cà phê sữa đá (18.000) + 1 xúc xích (5.000)
> → Tổng = 2×18.000 + 1×5.000 = **41.000 VNĐ** → trừ vào số dư

#### 7️⃣ Đổi gói cước (`PUT /user/{id}/category?category=VIP`)
- Khách có thể nâng/hạ gói bất kỳ lúc nào
- Phiên tiếp theo sẽ tính theo giá gói mới

### Tổng quan luồng nghiệp vụ

```
  ┌────────────────┐     ┌──────────────┐     ┌──────────────────────┐
  │  TẠO TÀI KHOẢN │───▶ │   NẠP TIỀN   │───▶ │  SỬ DỤNG INTERNET    │
  │  (chọn gói)    │     │  (balance ↑) │     │  start → ... → end   │
  └────────────────┘     └──────┬───────┘     │  (balance ↓ theo giờ)│
                                │             └──────────┬───────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────┐     ┌───────────────────────┐
                       │  GỌI ĐỒ ĂN   │───▶ │  ĐẶT HÀNG             │
                       │  (thêm giỏ)  │     │  (balance ↓ theo bill)│
                       └──────────────┘     └───────────────────────┘

  🔄 Mọi thay đổi số dư đều được ghi vào bảng user_balance_transactions
```

---

## 🗄 Cấu trúc Database & Flyway

### Flyway là gì?

**Flyway** là công cụ quản lý phiên bản database (database migration). Thay vì dùng `schema.sql` + `data.sql` tự chạy mỗi lần restart (mất dữ liệu), Flyway:

- ✅ Chạy migration **theo thứ tự phiên bản** (V1, V2, V3...)
- ✅ **Chỉ chạy 1 lần** — migration đã chạy sẽ không chạy lại
- ✅ Theo dõi lịch sử migration trong bảng `flyway_schema_history`
- ✅ Phát hiện **thay đổi trái phép** (checksum mismatch)
- ✅ Hỗ trợ mọi DB: H2, PostgreSQL, MySQL, Oracle, SQL Server...

### Quy ước đặt tên file migration

```
V{version}__{description}.sql

Ví dụ:
  V1__create_schema.sql       ← Phiên bản 1: tạo bảng
  V2__create_indexes.sql      ← Phiên bản 2: tạo index
  V3__seed_users.sql          ← Phiên bản 3: seed dữ liệu user
  V4__seed_items.sql          ← Phiên bản 4: seed dữ liệu menu
  V5__seed_orders.sql         ← Phiên bản 5: seed dữ liệu đơn hàng

Lưu ý:
  - Prefix "V" + số phiên bản
  - Hai dấu gạch dưới "__" phân cách version và description
  - Không được sửa file migration đã chạy (sẽ lỗi checksum)
  - Muốn thay đổi schema → tạo file migration MỚI (V6, V7...)
```

### Cấu trúc thư mục Flyway

```
src/main/resources/
└── db/
    └── migration/
        ├── V1__create_schema.sql      # DDL: Tạo 8 bảng + foreign keys
        ├── V2__create_indexes.sql     # DDL: Tạo indexes tối ưu truy vấn
        ├── V3__seed_users.sql         # DML: Seed gói cước, users, giao dịch, phiên
        ├── V4__seed_items.sql         # DML: Seed menu đồ ăn & thức uống
        └── V5__seed_orders.sql        # DML: Seed giỏ hàng, đơn hàng, chi tiết đơn
```

### Chi tiết từng migration

| File | Loại | Nội dung |
|------|------|----------|
| `V1__create_schema.sql` | DDL | Tạo 8 bảng: `categories`, `users`, `user_balance_transactions`, `sessions`, `items`, `cart_item`, `orders`, `order_item` với đầy đủ PK, FK, constraints |
| `V2__create_indexes.sql` | DDL | Tạo indexes trên các cột hay truy vấn: `username`, `user_id`, `status`, `order_status`, `item_type` |
| `V3__seed_users.sql` | DML | Insert 3 gói cước, 21 users, 21 giao dịch nạp tiền, 3 phiên mẫu |
| `V4__seed_items.sql` | DML | Insert 50 món: 30 đồ ăn (FOOD) + 20 thức uống (DRINK) |
| `V5__seed_orders.sql` | DML | Insert 4 giỏ hàng, 45 đơn hàng, ~200 chi tiết đơn hàng |

### Cấu hình Flyway trong `application.properties`

```properties
# Flyway
spring.flyway.enabled=true                          # Bật Flyway
spring.flyway.locations=classpath:db/migration       # Thư mục chứa migration files
spring.flyway.baseline-on-migrate=true               # Tự tạo baseline nếu DB đã có dữ liệu

# JPA — validate schema sau khi Flyway migrate
spring.jpa.hibernate.ddl-auto=validate

# Tắt cơ chế SQL init cũ (thay bằng Flyway)
spring.sql.init.mode=never
```

### Bảng `flyway_schema_history` (tự động tạo bởi Flyway)

Khi app khởi động, Flyway tạo bảng `flyway_schema_history` để theo dõi lịch sử migration:

```sql
SELECT installed_rank, version, description, type, checksum, installed_on, success
FROM flyway_schema_history;
```

| installed_rank | version | description    | type | success |
|---------------|---------|----------------|------|---------|
| 1             | 1       | create schema  | SQL  | true    |
| 2             | 2       | create indexes | SQL  | true    |
| 3             | 3       | seed users     | SQL  | true    |
| 4             | 4       | seed items     | SQL  | true    |
| 5             | 5       | seed orders    | SQL  | true    |

### Cách thêm migration mới

Khi cần thay đổi database (thêm bảng, thêm cột, sửa constraint...):

```bash
# 1. Tạo file migration mới (KHÔNG sửa file cũ)
touch src/main/resources/db/migration/V6__add_phone_to_users.sql

# 2. Viết SQL trong file
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

# 3. Restart app → Flyway tự chạy V6
./gradlew bootRun
```

### Chuyển sang PostgreSQL / MySQL (production)

Chỉ cần đổi datasource trong `application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/internetshop
spring.datasource.username=postgres
spring.datasource.password=secret
spring.datasource.driver-class-name=org.postgresql.Driver

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/internetshop
spring.datasource.username=root
spring.datasource.password=secret
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

> ⚠️ Lưu ý: SQL syntax trong migration cần tương thích với DB đích. H2 `MODE=PostgreSQL` đã giúp tương thích phần lớn syntax.

---

## 📊 ER Diagram

Hệ thống gồm **8 bảng**, chia thành 3 nhóm chức năng:

| Nhóm | Bảng | Chức năng |
|------|------|-----------|
| **Người dùng** | `categories`, `users`, `user_balance_transactions` | Quản lý tài khoản, gói cước, lịch sử giao dịch |
| **Internet** | `sessions` | Theo dõi phiên sử dụng, tính giờ |
| **Đồ ăn/uống** | `items`, `cart_item`, `orders`, `order_item` | Menu, giỏ hàng, đặt hàng |

```
 ┌──────────────────┐          ┌──────────────────────────┐
 │    categories    │          │         users            │
 ├──────────────────┤          ├──────────────────────────┤
 │ PK id        INT │◄────┐    │ PK id            INT     │
 │    name      VCR │     │    │    username       VCR  UQ│
 │    price/h   INT │     └────┤ FK category_id    INT    │
 │                  │          │    full_name      VCR    │
 │  NORMAL = 10000  │          │    password       VCR    │
 │  VIP    = 15000  │          │    balance        INT    │
 │  VVIP   = 20000  │          │    created_at     TS     │
 └──────────────────┘          └─────┬──────┬──────┬──────┘
                                     │      │      │
                    ┌────────────────┘      │      └────────────────┐
                    │                       │                       │
                    ▼                       ▼                       ▼
 ┌────────────────────────┐  ┌────────────────────┐  ┌────────────────────────┐
 │ user_balance_          │  │     sessions       │  │       orders            │
 │ transactions           │  ├────────────────────┤  ├────────────────────────┤
 ├────────────────────────┤  │ PK id          INT │  │ PK id            INT   │
 │ PK id           INT    │  │ FK user_id     INT │  │ FK user_id       INT   │
 │ FK user_id      INT    │  │ FK category_id INT │  │    discount      INT   │
 │    amount       INT    │  │    start_time  TS  │  │    order_status  INT   │
 │    type         INT    │  │    end_time    TS  │  │    order_date    DATE  │
 │    1=DEPOSIT           │  │    price/h     INT │  │    total_amount  INT   │
 │    2=ORDER_PAYMENT     │  │    status      INT │  │                        │
 │    3=SESSION_PAYMENT   │  │    1=ACTIVE        │  │  1=NEW  2=DONE  3=CANCEL│
 │    description  VCR    │  │    2=EXPIRED       │  └───────────┬────────────┘
 │    created_at   TS     │  │    3=CANCELLED     │              │
 └────────────────────────┘  └────────────────────┘              │ 1:N
                                                                 ▼
 ┌────────────────────────┐                       ┌────────────────────────┐
 │       items            │                       │      order_item        │
 ├────────────────────────┤                       ├────────────────────────┤
 │ PK id          INT     │◄──────────────────────┤ PK,FK order_id   INT   │
 │    name        VCR     │                       │ PK,FK item_id    INT   │
 │    price       INT     │                       │       quantity   INT   │
 │    item_type   INT     │                       │       price      INT   │
 │    1=FOOD  2=DRINK     │                       └────────────────────────┘
 └───────────┬────────────┘
             │
             │◄─────────────────────────────────┐
             ▼                                  │
 ┌────────────────────────┐                     │
 │      cart_item         │        users.id ────►│
 ├────────────────────────┤                      │
 │ PK,FK user_id    INT   │──────────────────────┘
 │ PK,FK item_id    INT   │
 │       quantity   INT   │
 └────────────────────────┘

 ┌────────────────────────────┐
 │  flyway_schema_history     │  ← Bảng do Flyway tự tạo
 ├────────────────────────────┤
 │  installed_rank  INT       │
 │  version         VCR       │
 │  description     VCR       │
 │  type            VCR       │
 │  script          VCR       │
 │  checksum        INT       │
 │  installed_on    TS        │
 │  success         BOOLEAN   │
 └────────────────────────────┘
```

### Quan hệ giữa các bảng

```
categories  (1) ◄──── (N)  users                    Mỗi user thuộc 1 gói
users       (1) ────► (N)  user_balance_transactions Mỗi user có nhiều giao dịch
users       (1) ────► (N)  sessions                  Mỗi user có nhiều phiên
users       (1) ────► (N)  orders                    Mỗi user có nhiều đơn hàng
users       (1) ────► (N)  cart_item                 Mỗi user có nhiều item trong giỏ
categories  (1) ◄──── (N)  sessions                  Mỗi phiên thuộc 1 gói cước
orders      (1) ────► (N)  order_item                Mỗi đơn hàng có nhiều item
items       (1) ◄──── (N)  order_item                Mỗi item thuộc nhiều đơn hàng
items       (1) ◄──── (N)  cart_item                 Mỗi item có trong nhiều giỏ hàng
```

---

## 📝 Chi tiết các bảng

### `categories` — Gói cước internet
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã gói |
| `name` | VARCHAR(50) | NOT NULL | Tên gói: NORMAL / VIP / VVIP |
| `price_per_hour` | INTEGER | NOT NULL | Đơn giá mỗi giờ (VNĐ) |

### `users` — Tài khoản người dùng
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã người dùng |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Tên đăng nhập |
| `full_name` | VARCHAR(255) | NOT NULL | Họ tên |
| `password` | VARCHAR(255) | NOT NULL | Mật khẩu |
| `balance` | INTEGER | NOT NULL, DEFAULT 0 | Số dư hiện tại (VNĐ) |
| `category_id` | INTEGER | FK → categories | Gói cước đang dùng |
| `created_at` | TIMESTAMP | NOT NULL | Thời điểm tạo tài khoản |

### `user_balance_transactions` — Lịch sử giao dịch số dư
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã giao dịch |
| `user_id` | INTEGER | FK → users | Người dùng |
| `amount` | INTEGER | NOT NULL | Số tiền (+ nạp, − trừ) |
| `type` | INTEGER | NOT NULL | 1=Nạp tiền, 2=Thanh toán đơn hàng, 3=Thanh toán phiên |
| `description` | VARCHAR(255) | | Ghi chú |
| `created_at` | TIMESTAMP | NOT NULL | Thời điểm giao dịch |

### `sessions` — Phiên sử dụng internet
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã phiên |
| `user_id` | INTEGER | FK → users | Người dùng |
| `category_id` | INTEGER | FK → categories | Gói cước tại thời điểm bắt đầu |
| `start_time` | TIMESTAMP | NOT NULL | Thời điểm bắt đầu |
| `end_time` | TIMESTAMP | NULL nếu đang active | Thời điểm kết thúc |
| `price_per_hour` | INTEGER | NOT NULL | Snapshot giá/giờ lúc bắt đầu |
| `status` | INTEGER | NOT NULL | 1=Đang dùng, 2=Hết hạn, 3=Đã hủy |

### `items` — Menu đồ ăn / thức uống
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã món |
| `name` | VARCHAR(255) | NOT NULL | Tên món |
| `price` | INTEGER | NOT NULL | Giá (VNĐ) |
| `item_type` | INTEGER | | 1=Đồ ăn, 2=Thức uống |

### `cart_item` — Giỏ hàng
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `user_id` | INTEGER | PK, FK → users | Người dùng |
| `item_id` | INTEGER | PK, FK → items | Món hàng |
| `quantity` | INTEGER | NOT NULL | Số lượng |

### `orders` — Đơn hàng
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | INTEGER | PK, AUTO | Mã đơn |
| `user_id` | INTEGER | FK → users | Người đặt |
| `discount` | INTEGER | NOT NULL, DEFAULT 0 | Giảm giá (VNĐ) |
| `order_status` | INTEGER | NOT NULL | 1=Mới, 2=Hoàn thành, 3=Đã hủy |
| `order_date` | DATE | NOT NULL | Ngày đặt |
| `total_amount` | INTEGER | | Tổng tiền sau giảm giá |

### `order_item` — Chi tiết đơn hàng
| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `order_id` | INTEGER | PK, FK → orders | Mã đơn |
| `item_id` | INTEGER | PK, FK → items | Mã món |
| `quantity` | INTEGER | NOT NULL | Số lượng |
| `price` | INTEGER | NOT NULL | Giá tại thời điểm đặt |

---

## 🌐 REST API

### 👤 Quản lý người dùng (`/user`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/user` | Tạo tài khoản mới |
| `GET` | `/user` | Danh sách tất cả người dùng |
| `GET` | `/user/{id}` | Thông tin user (kèm thời gian còn lại) |
| `GET` | `/user/username/{username}` | Tìm user theo username |
| `POST` | `/user/deposit` | Nạp tiền vào tài khoản |
| `PUT` | `/user/{id}/category?category=VIP` | Đổi gói cước |
| `GET` | `/user/{id}/transactions` | Lịch sử giao dịch |

**Tạo tài khoản:**
```json
POST /user
{
  "username": "player01",
  "fullName": "Nguyen Van A",
  "password": "123456",
  "category": "VIP"
}
```

**Nạp tiền:**
```json
POST /user/deposit
{
  "userId": 1,
  "amount": 200000,
  "description": "Nạp tiền tại quầy"
}
```

### 🖥️ Quản lý phiên internet (`/session`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/session/start/{userId}` | Bắt đầu phiên (bắt đầu tính giờ) |
| `POST` | `/session/end/{userId}` | Kết thúc phiên (trừ tiền theo giờ dùng) |
| `GET` | `/session/time-remaining/{userId}` | Xem thời gian còn lại |
| `GET` | `/session/history/{userId}` | Lịch sử các phiên |

**Response mẫu — Thời gian còn lại:**
```json
GET /session/time-remaining/1
{
  "userId": 1,
  "username": "ToanPDT",
  "category": "NORMAL",
  "balance": 500000,
  "pricePerHour": 10000,
  "remainingHours": 50,
  "remainingMinutes": 0,
  "remainingTimeFormatted": "50h 0m",
  "hasActiveSession": false
}
```

### 🍔 Menu đồ ăn / thức uống (`/item`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/item/search` | Tất cả món |
| `GET` | `/item/search?key=cà phê` | Tìm theo tên |
| `GET` | `/item/search?key=33` | Tìm theo id hoặc tên chứa "33" |
| `GET` | `/item/food` | Chỉ đồ ăn |
| `GET` | `/item/drink` | Chỉ thức uống |

### 🛒 Giỏ hàng (`/cart`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/cart?userId=1` | Xem giỏ hàng |
| `POST` | `/cart/quantity` | Thêm/sửa/xóa món trong giỏ |

```json
POST /cart/quantity
{ "userId": 1, "itemId": 33, "quantity": 2 }   // Thêm 2 ly cà phê đen đá
{ "userId": 1, "itemId": 33, "quantity": 5 }   // Sửa thành 5 ly
{ "userId": 1, "itemId": 33, "quantity": 0 }   // Xóa khỏi giỏ
```

### 📦 Đơn hàng (`/order`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/order` | Tạo đơn từ giỏ hàng (trừ tiền, xóa giỏ) |
| `GET` | `/order/search` | Tất cả đơn hàng |
| `GET` | `/order/search?userId=1` | Đơn hàng theo user |
| `GET` | `/order/search?orderStatus=NEW` | Đơn hàng theo trạng thái |

```json
POST /order
{ "userId": 1, "discount": 5000 }
```

---

## 📊 SQL mẫu

### Xem thời gian còn lại của user
```sql
SELECT u.username, u.balance, c.name AS category, c.price_per_hour,
       u.balance / c.price_per_hour AS remaining_hours,
       (u.balance % c.price_per_hour) * 60 / c.price_per_hour AS remaining_minutes
FROM users u
JOIN categories c ON u.category_id = c.id
WHERE u.id = 1;
```

### Top 3 user chi tiêu nhiều nhất
```sql
SELECT sub.user_id, SUM(sub.order_total) AS total_spent
FROM (
    SELECT o.id, o.user_id,
           COALESCE(SUM(oi.quantity * oi.price), 0) - o.discount AS order_total
    FROM orders o
    LEFT JOIN order_item oi ON o.id = oi.order_id
    WHERE o.order_status = 2  -- Chỉ tính đơn DONE
    GROUP BY o.id, o.user_id, o.discount
) sub
GROUP BY sub.user_id
ORDER BY total_spent DESC
LIMIT 3;
```

### Top 5 món bán chạy nhất Q4/2025
```sql
SELECT oi.item_id, i.name, SUM(oi.quantity) AS total_qty,
       SUM(oi.quantity * oi.price) AS total_revenue
FROM order_item oi
JOIN orders o ON oi.order_id = o.id
JOIN items i ON oi.item_id = i.id
WHERE o.order_status = 2
  AND o.order_date BETWEEN '2025-10-01' AND '2025-12-31'
GROUP BY oi.item_id, i.name
ORDER BY total_revenue DESC
LIMIT 5;
```

### Xem lịch sử Flyway migration
```sql
SELECT installed_rank, version, description, script, checksum, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

