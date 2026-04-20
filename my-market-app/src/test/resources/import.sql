-- Очищаем таблицу
TRUNCATE TABLE products RESTART IDENTITY CASCADE;

-- Добавляем продукты (используем одинарные кавычки и экранируем внутренние)
INSERT INTO products (id, title, description, img_path, price, count) 
VALUES 
(1, 'Ноутбук Apple MacBook Pro 14', 'M3 Pro чип, 16GB RAM, 512GB SSD, 14.2-дюймовый дисплей', '/images/products/macbook-pro-14.jpg', 199999, 10)