package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ru.yandex.practicum.mymarket.entity.OrderProductEntity;

@Repository
public interface OrderProductRepository extends ReactiveCrudRepository<OrderProductEntity, Long>{
    Flux<OrderProductEntity> findByOrderId(Long orderId);
}
