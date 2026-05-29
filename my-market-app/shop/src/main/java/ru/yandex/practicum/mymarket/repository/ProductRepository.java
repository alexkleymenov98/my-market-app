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
}
