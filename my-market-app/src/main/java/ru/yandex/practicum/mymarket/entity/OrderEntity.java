package ru.yandex.practicum.mymarket.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalSum;

     @ManyToMany
    @JoinTable(
        name = "order_items",  // связующая таблица
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<ProductEntity> items;

    public Long getId() {
        return id;
    }

    public Long getTotalSum(){
        return totalSum;
    }

    public List<ProductEntity> getItems(){
        return items;
    }
}
