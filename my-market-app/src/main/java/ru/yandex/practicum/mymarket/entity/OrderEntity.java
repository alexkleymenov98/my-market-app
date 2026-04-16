package ru.yandex.practicum.mymarket.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalSum;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductEntity> orderProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Long getTotalSum(){
        return totalSum;
    }

    public void setTotalSum(Long totalSum){
        this.totalSum = totalSum;
    }

     public List<ProductEntity> getItems() {
        return orderProducts.stream()
            .map(OrderProductEntity::getProduct)
            .collect(Collectors.toList());
    }

    public void setItems(List<OrderProductEntity> items){
        this.orderProducts = items;
    }

    public void addProduct(ProductEntity item){
        OrderProductEntity orderProduct = new OrderProductEntity();
        orderProduct.setOrder(this);
        orderProduct.setProduct(item);
        orderProduct.setCount(item.getCount());
        this.orderProducts.add(orderProduct);

    }
}
