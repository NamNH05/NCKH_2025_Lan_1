-- SQL Server Database Initialization Script for Order Service
-- Database: order_dB

USE master;
GO

-- Create database if not exists
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'order_dB')
BEGIN
    CREATE DATABASE order_dB;
END
GO

USE order_dB;
GO

-- ============================================
-- DROP TABLES IN CORRECT ORDER (respecting foreign keys)
-- ============================================

IF OBJECT_ID('dbo.OrderItem', 'U') IS NOT NULL
    DROP TABLE dbo.OrderItem;

IF OBJECT_ID('dbo.Payment', 'U') IS NOT NULL
    DROP TABLE dbo.Payment;

IF OBJECT_ID('dbo.Shipment', 'U') IS NOT NULL
    DROP TABLE dbo.Shipment;

IF OBJECT_ID('dbo.Orders', 'U') IS NOT NULL
    DROP TABLE dbo.Orders;

IF OBJECT_ID('dbo.CartItem', 'U') IS NOT NULL
    DROP TABLE dbo.CartItem;

IF OBJECT_ID('dbo.Cart', 'U') IS NOT NULL
    DROP TABLE dbo.Cart;

GO

-- ============================================
-- ORDER SERVICE TABLES
-- ============================================

-- Cart table
CREATE TABLE dbo.Cart (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'CART' CHECK (status IN ('CART', 'ORDERED')),
    total DECIMAL(18, 2) DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE INDEX idx_user_status ON dbo.Cart(user_id, status);
GO

-- CartItem table
CREATE TABLE dbo.CartItem (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    cart_id BIGINT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_CartItem_Cart FOREIGN KEY (cart_id) REFERENCES dbo.Cart(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_id ON dbo.CartItem(cart_id);
GO

-- Create Order table
CREATE TABLE dbo.Orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    customer_id BIGINT NOT NULL,
    total DECIMAL(18, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    address NVARCHAR(500),
    note NVARCHAR(MAX),
    item_details NVARCHAR(MAX),
    shipping_fee DECIMAL(18, 2),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE INDEX idx_customer_id ON dbo.Orders(customer_id);
CREATE INDEX idx_status ON dbo.Orders(status);
CREATE INDEX idx_created_at ON dbo.Orders(created_at);
GO

-- OrderItem table (detail items in order)
CREATE TABLE dbo.OrderItem (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name NVARCHAR(255) NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(18, 2),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_OrderItem_Orders FOREIGN KEY (order_id) REFERENCES dbo.Orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_id ON dbo.OrderItem(order_id);
GO

-- Payment table
CREATE TABLE dbo.Payment (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(18, 2) NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    transaction_id VARCHAR(255),
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Payment_Orders FOREIGN KEY (order_id) REFERENCES dbo.Orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_payment ON dbo.Payment(order_id);
CREATE INDEX idx_payment_status ON dbo.Payment(payment_status);
GO

-- Shipment table
CREATE TABLE dbo.Shipment (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL UNIQUE,
    tracking_number VARCHAR(100),
    carrier VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PICKED', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'FAILED')),
    address NVARCHAR(500),
    shipped_date DATETIME,
    delivery_date DATETIME,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Shipment_Orders FOREIGN KEY (order_id) REFERENCES dbo.Orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_shipment ON dbo.Shipment(order_id);
CREATE INDEX idx_shipment_tracking ON dbo.Shipment(tracking_number);
CREATE INDEX idx_shipment_status ON dbo.Shipment(status);
GO

-- ============================================
-- VERIFICATION
-- ============================================

PRINT 'Order Service SQL Server Database Initialized Successfully!';

-- List all created tables
SELECT 
    table_name = TABLE_NAME,
    row_count = (
        SELECT COUNT(*) 
        FROM (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS c 
            WHERE c.TABLE_NAME = INFORMATION_SCHEMA.TABLES.TABLE_NAME
        ) count_data
    )
FROM 
    INFORMATION_SCHEMA.TABLES 
WHERE 
    TABLE_SCHEMA = 'dbo' AND TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    TABLE_NAME;
GO

PRINT 'Total tables created: 6 (Cart, CartItem, Orders, OrderItem, Payment, Shipment)';
PRINT 'Order Service database initialized successfully!';
GO
