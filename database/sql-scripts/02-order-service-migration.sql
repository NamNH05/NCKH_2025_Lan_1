-- ALTER TABLE MIGRATION - Order Service (SQL Server)
-- This script adds new columns to the Orders table to support item details and shipping fee
-- Database: order_dB

USE order_dB;
GO

-- Add item_details column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='Orders' AND COLUMN_NAME='item_details')
BEGIN
    ALTER TABLE dbo.Orders
    ADD item_details NVARCHAR(MAX);
    PRINT 'Column item_details added to Orders table';
END
ELSE
BEGIN
    PRINT 'Column item_details already exists in Orders table';
END
GO

-- Add shipping_fee column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='Orders' AND COLUMN_NAME='shipping_fee')
BEGIN
    ALTER TABLE dbo.Orders
    ADD shipping_fee DECIMAL(18, 2);
    PRINT 'Column shipping_fee added to Orders table';
END
ELSE
BEGIN
    PRINT 'Column shipping_fee already exists in Orders table';
END
GO

-- Verify the changes
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Orders'
ORDER BY ORDINAL_POSITION;
GO

PRINT 'Migration completed successfully!';
GO
