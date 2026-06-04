package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.CartProductEntity;
import ru.yandex.practicum.mymarket.entity.OrderEntity;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {
    @Query("SELECT * FROM orders WHERE username = :username")
    Flux<OrderEntity> findOrders(@Param("username") String username);

    Mono<OrderEntity> findOrderByIdAndUsername(@Param("id") Long id, @Param("username") String username);
}