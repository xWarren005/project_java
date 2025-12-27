-- 0. Tạo Database chung
CREATE DATABASE IF NOT EXISTS S2O_db;
USE S2O_db;

-- 1. Bảng User
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    phone VARCHAR(20),
    avatar_url TEXT,
    role ENUM('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER') DEFAULT 'CUSTOMER',
    ai_preferences TEXT COMMENT 'Sở thích cá nhân cho AI phân tích',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Restaurant
CREATE TABLE IF NOT EXISTS restaurants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phone_contact VARCHAR(50),
    location_lat FLOAT,
    location_long FLOAT,
    description TEXT,
    logo_url VARCHAR(255),
    cover_image VARCHAR(255),
    avg_rating FLOAT DEFAULT 0.0,
    bank_qr_config JSON,
    ai_config JSON,
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Bảng Table (Quản lý bàn tại chỗ)
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    table_name VARCHAR(100),
    qr_code_string VARCHAR(255),
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED') DEFAULT 'AVAILABLE',
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 4. Bảng Category (Danh mục món ăn)
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_order INT DEFAULT 0,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 5. Bảng Product (Món ăn/Sản phẩm)
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    ai_generated BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 6. Bảng Promotion (Khuyến mãi)
CREATE TABLE IF NOT EXISTS promotions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    code VARCHAR(50) NOT NULL,
    discount_percent DECIMAL(5, 2),
    max_discount DECIMAL(10, 2),
    start_date DATETIME,
    end_date DATETIME,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 7. Bảng Reservation (Đặt bàn trước)
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    booking_time DATETIME NOT NULL,
    guest_count INT DEFAULT 1,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'ARRIVED') DEFAULT 'PENDING',
    note TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 8. Bảng Order (Đơn hàng - Hợp nhất từ Order và order_history)
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    table_id INT NULL COMMENT 'Có thể NULL nếu mua mang về hoặc đặt online',
    user_id INT NULL,
    promotion_id INT NULL,
    total_amount DECIMAL(10, 2) DEFAULT 0,
    status ENUM('PENDING', 'CONFIRMED', 'SERVED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE SET NULL
);

-- 9. Bảng OrderItem (Chi tiết món ăn trong đơn)
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 10. Bảng Invoice (Hóa đơn thanh toán)
CREATE TABLE IF NOT EXISTS invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNIQUE NOT NULL,
    payment_method ENUM('CASH', 'BANK_TRANSFER', 'E-WALLET'),
    amount_paid DECIMAL(10, 2) NOT NULL,
    transaction_ref VARCHAR(255),
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- 11. Bảng User Review (Đánh giá từ khách hàng)
CREATE TABLE IF NOT EXISTS user_reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    image_urls JSON COMMENT 'Lưu danh sách link ảnh dưới dạng JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- 12. Bảng Loyalty Membership (Khách hàng thân thiết)
CREATE TABLE IF NOT EXISTS loyalty_memberships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    total_points INT DEFAULT 0,
    tier_level VARCHAR(20) DEFAULT 'Bronze',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);