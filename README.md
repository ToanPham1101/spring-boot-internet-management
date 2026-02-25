# Item API

## Giới thiệu
- Java 17
- Gradle 7.6.1
- Spring Boot 3.1.0 (Web, JPA)
- Lombok
- Database H2
  - gồm các bảng items, orders, order_item
  - bảng và dữ liệu được tự động tạo khi start app (xem thêm [schema.sql](src/main/resources/schema.sql) và [data.sql](src/main/resources/data.sql))
  - Cách vào database
    - username/password là sa/123456
    - dùng plugin database của IntelliJ IDEA, JDBC URL `jdbc:h2:tcp://localhost:9092/mem:test`
    - mở http://localhost:8080/h2, JDBC URL `jdbc:h2:mem:test`
- Swagger
  - mở http://localhost:8080 để vào màn hình Swagger, xem và test api

#### 1
`GET /item/search` Tìm kiếm sản phẩm theo key. key là id hoặc 1 phần của tên. Ví dụ
- GET `/item/search` trả về toàn bộ sản phẩm
- GET `/item/search?key=abc` trả về các sản phẩm có tên chứa abc
- GET `/item/search?key=123` trả về sản phẩm có id=123 và các sản phẩm có tên chứa 123

#### 2
GET `/order/search` Tìm kiếm đơn hàng theo user hoặc status. Ví dụ
- GET `/order/search` trả về toàn bộ đơn hàng
- GET `/order/search?userId=hblab` trả về các đơn hàng của hblab
- GET `/order/search?orderStatus=NEW` trả về các đơn hàng mới
- GET `/order/search?userId=hblab&orderStatus=CANCEL` trả về các đơn hàng đã hủy của hblab

#### 3
`GET /cart` Lấy các sản phẩm có trong giỏ hàng của user. Thông tin trả về gồm có
- id sản phẩm
- tên sản phẩm
- số lượng có trong giỏ hàng

#### 4
`POST /cart/quantity` Cập nhật số lượng  trong giỏ hàng. Chú ý các case sau
- trong giỏ hàng chưa có sản phẩm
- trong giỏ hàng đã có sản phẩm
- cập nhật số lượng về 0 thì xóa sản phẩm có trong giỏ hàng

#### 5
`POST /order` Tạo đơn hàng mới từ các sản phẩm có trong giỏ và xóa các sản phẩm có trong giỏ hàng
