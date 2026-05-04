package ru.yandex.practicum.mymarket.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_product")
public class OrderProductEntity {
    @Id
    private Long id;
    
    private Integer count; 

    private Long orderId;
    
    private Long productId;


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // public ProductEntity getProduct() {
    //     ProductEntity item = new ProductEntity();
    //     item.setId(id);
    //     item.setTitle(product.getTitle());
    //     item.setDescription(product.getDescription());
    //     item.setImgPath(product.getImgPath());
    //     item.setPrice(product.getPrice());

    //     item.setCount(getCount());

    //     return item;
    // }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductId(){
        return productId;
    }

    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}
