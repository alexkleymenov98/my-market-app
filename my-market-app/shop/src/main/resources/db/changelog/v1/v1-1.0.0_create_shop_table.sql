-- changeset author:1
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY, 
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    img_path VARCHAR(255),
    price BIGINT,
    count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY, 
    total_sum BIGINT

);

CREATE TABLE IF NOT EXISTS order_product (
    id BIGSERIAL PRIMARY KEY, 
    count INTEGER NOT NULL DEFAULT 0,
    order_id BIGINT,
    product_id BIGINT,
    CONSTRAINT fk_order_product_order 
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_product_product 
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);