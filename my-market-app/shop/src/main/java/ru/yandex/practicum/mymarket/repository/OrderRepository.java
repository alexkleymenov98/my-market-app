package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.mymarket.entity.OrderEntity;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {
  
}