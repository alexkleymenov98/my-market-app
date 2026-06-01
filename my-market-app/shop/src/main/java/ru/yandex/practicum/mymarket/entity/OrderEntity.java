package ru.yandex.practicum.mymarket.entity;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "orders")
public class OrderEntity {
    @Id
    private Long id;
    private Long totalSum;
    private String username;

    @Transient
    private transient List<ProductEntity> orderProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotalSum(){
        return totalSum;
    }

    public void setTotalSum(Long totalSum){
        this.totalSum = totalSum;
    }

    public List<ProductEntity> getItems() {
        return orderProducts;
    }

    public void setItems(List<ProductEntity> items){
        this.orderProducts = items;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addProducts(List<ProductEntity> products) {
        this.orderProducts.addAll(products);
    }
}
