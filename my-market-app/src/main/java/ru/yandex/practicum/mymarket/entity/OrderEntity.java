package ru.yandex.practicum.mymarket.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "orders")
public class OrderEntity {
    @Id
    private Long id;
    private Long totalSum;

    private transient List<OrderProductEntity> orderProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Long getTotalSum(){
        return totalSum;
    }

    public void setTotalSum(Long totalSum){
        this.totalSum = totalSum;
    }

    public List<Long> getItems() {
        return orderProducts.stream()
            .map(OrderProductEntity::getProductId)
            .collect(Collectors.toList());
    }

    public void setItems(List<OrderProductEntity> items){
        this.orderProducts = items;
    }

    // public void addProduct(ProductEntity item){
    //     OrderProductEntity orderProduct = new OrderProductEntity();
    //     orderProduct.setOrder(this);
    //     orderProduct.setProduct(item);
    //     orderProduct.setCount(item.getCount());
    //     this.orderProducts.add(orderProduct);

    // }
}
