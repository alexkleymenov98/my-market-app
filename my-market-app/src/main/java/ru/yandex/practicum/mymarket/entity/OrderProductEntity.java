package ru.yandex.practicum.mymarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_product")
public class OrderProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    
    @Column(name = "count")
    private Integer count; 

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public ProductEntity getProduct() {
        ProductEntity item = new ProductEntity();
        item.setId(id);
        item.setTitle(product.getTitle());
        item.setDescription(product.getDescription());
        item.setImgPath(product.getImgPath());
        item.setPrice(product.getPrice());

        item.setCount(getCount());

        return item;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}
