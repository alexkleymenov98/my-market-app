CREATE TABLE IF NOT EXISTS cart_product (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT,
    count INTEGER NOT NULL DEFAULT 0,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT fk_order_product_product
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

ALTER TABLE products DROP COLUMN count;

ALTER TABLE orders
    ADd username VARCHAR(255) NOT null default 'user';