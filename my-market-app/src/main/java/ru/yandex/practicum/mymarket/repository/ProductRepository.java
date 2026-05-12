package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;

@Repository
public interface ProductRepository extends R2dbcRepository<ProductEntity, Long>{
    Mono<ProductEntity> findById(Long id);
    Flux<ProductEntity> findByCountGreaterThan(int count);

    @Modifying
    @Transactional
    @Query("UPDATE products  SET count = count + 1 WHERE id = :id")
    Mono<Void> incrementCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE products  SET count = count - 1 WHERE id = :id AND count > 0")
    Mono<Void> decrementCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE products  SET count = 0 WHERE id = :id")
    Mono<Void> setCountZero(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE products  SET count = 0 WHERE count > 0")
    Mono<Void> deleteAllFromCart();
}
