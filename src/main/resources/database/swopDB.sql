DROP DATABASE IF EXISTS swop_db;
CREATE DATABASE swop_db;
USE swop_db;

-- Create customers table
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    default_shipping_address TEXT,
    country VARCHAR(100),
    phone VARCHAR(20)
);

-- Create categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    thumbnail VARCHAR(255)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    weight DECIMAL(10,2),
    descriptions TEXT,
    image VARCHAR(255),
    category BIGINT,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stock INT NOT NULL DEFAULT 0, 
    FOREIGN KEY (category) REFERENCES categories(id) ON DELETE SET NULL
);


CREATE TABLE product_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    shipping_address TEXT NOT NULL,
    order_address TEXT NOT NULL,
    order_email VARCHAR(100) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_status ENUM('processed', 'delivered', 'canceled') DEFAULT 'processed',
    payment_method ENUM('credit_card', 'paypal', 'bank_transfer') NOT NULL, -- New payment method column
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Create order_details table (many-to-many between orders and products)
CREATE TABLE order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create a trigger to check and update stock before processing an order
DELIMITER $$

CREATE TRIGGER check_product_stock
BEFORE INSERT ON order_details
FOR EACH ROW
BEGIN
    DECLARE available_stock INT;

    -- Get available stock for the product
    SELECT stock INTO available_stock 
    FROM products 
    WHERE id = NEW.product_id;

    -- Check if there is enough stock
    IF available_stock IS NULL OR available_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock';
    ELSE
        -- Deduct stock before inserting the order
        UPDATE products
        SET stock = stock - NEW.quantity
        WHERE id = NEW.product_id;
    END IF;
END$$

DELIMITER ;
