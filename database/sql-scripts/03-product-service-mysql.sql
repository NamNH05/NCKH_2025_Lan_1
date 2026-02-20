-- MySQL Database Initialization Script for Product Service
-- Database: shopquanao

CREATE DATABASE IF NOT EXISTS shopquanao;
USE shopquanao;

-- Create Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    price DECIMAL(10, 2) NOT NULL,
    cost DECIMAL(10, 2),
    quantity INT DEFAULT 0,
    image_url VARCHAR(500),
    category VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_product_name (name),
    KEY idx_category (category),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Stock History table (for auditing)
CREATE TABLE IF NOT EXISTS stock_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    previous_quantity INT,
    new_quantity INT,
    change_type VARCHAR(50) COMMENT 'ADD, REMOVE, ADJUST',
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    KEY idx_product_id (product_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Product Images table
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample categories
INSERT INTO categories (name, description, status) VALUES
    ('Áo nam', 'Các loại áo cho nam giới', 'ACTIVE'),
    ('Áo nữ', 'Các loại áo cho nữ giới', 'ACTIVE'),
    ('Quần nam', 'Các loại quần cho nam giới', 'ACTIVE'),
    ('Quần nữ', 'Các loại quần cho nữ giới', 'ACTIVE'),
    ('Giày dép', 'Các loại giày và dép', 'ACTIVE'),
    ('Phụ kiện', 'Các loại phụ kiện thời trang', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample products
INSERT INTO products (name, description, price, cost, quantity, category, status) VALUES
    ('Áo phông trắng nam', 'Áo phông basic trắng 100% cotton', 99000, 50000, 50, 'Áo nam', 'ACTIVE'),
    ('Áo sơ mi xanh dương', 'Áo sơ mi nam màu xanh dương, chất liệu cotton thoáng mát', 299000, 150000, 30, 'Áo nam', 'ACTIVE'),
    ('Quần jean denim nam', 'Quần jean nam màu xanh đậm, kiểu dáng hiện đại', 399000, 200000, 25, 'Quần nam', 'ACTIVE'),
    ('Áo phông nữ hồng', 'Áo phông nữ màu hồng pastel, form fitted', 129000, 60000, 40, 'Áo nữ', 'ACTIVE'),
    ('Quần legging nữ', 'Quần legging nữ đen, co giãn tốt', 199000, 100000, 35, 'Quần nữ', 'ACTIVE'),
    ('Giày thể thao trắng', 'Giày thể thao unisex màu trắng', 599000, 300000, 20, 'Giày dép', 'ACTIVE'),
    ('Nón lưỡi trai', 'Nón lưỡi trai kiểu dáng thể thao', 149000, 75000, 45, 'Phụ kiện', 'ACTIVE'),
    ('Thắt lưng da nam', 'Thắt lưng da thật cho nam giới', 249000, 120000, 30, 'Phụ kiện', 'ACTIVE')
ON DUPLICATE KEY UPDATE price=VALUES(price), quantity=VALUES(quantity);

-- Create indexes for performance
CREATE INDEX idx_products_category_status ON products(category, status);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_stock_history_product_created ON stock_history(product_id, created_at);

-- Verify table creation
SELECT 'Product Service MySQL Database Initialized Successfully!' as status;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'shopquanao';
