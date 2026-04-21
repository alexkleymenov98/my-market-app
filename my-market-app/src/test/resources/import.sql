-- Очищаем таблицу
TRUNCATE TABLE products RESTART IDENTITY CASCADE;

-- Добавляем продукты (используем одинарные кавычки и экранируем внутренние)
INSERT INTO products (id, title, description, img_path, price, count) 
VALUES 
(1, 'Ноутбук Apple MacBook Pro 14', 'M3 Pro чип, 16GB RAM, 512GB SSD, 14.2-дюймовый дисплей', '/images/products/macbook-pro-14.jpg', 199999, 10),
(2, 'Ноутбук Apple MacBook Air 13', 'M3 чип, 8GB RAM, 256GB SSD, 13.6-дюймовый дисплей', '/images/products/macbook-air-13.jpg', 119999, 15),
(3, 'Ноутбук ASUS ROG Zephyrus G16', 'Intel Core i9, 32GB RAM, 1TB SSD, RTX 4080, 16-дюймовый дисплей', '/images/products/rog-zephyrus-g16.jpg', 249999, 5),
(4, 'Ноутбук Lenovo ThinkPad X1 Carbon', 'Intel Core i7, 16GB RAM, 512GB SSD, 14-дюймовый дисплей', '/images/products/thinkpad-x1.jpg', 179999, 8),
(5, 'Ноутбук Dell XPS 15', 'Intel Core i7, 32GB RAM, 1TB SSD, RTX 4060, 15.6-дюймовый дисплей', '/images/products/dell-xps-15.jpg', 189999, 7);