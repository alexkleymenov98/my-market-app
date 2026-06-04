package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;

@Repository
public interface ProductRepository extends R2dbcRepository<ProductEntity, Long>{
    Mono<ProductEntity> findById(Long id);
    Flux<ProductEntity> findByCountGreaterThan(int count);
}
