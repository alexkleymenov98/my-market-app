package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.CartProductEntity;



@Repository
public interface CartProductRepository extends R2dbcRepository<CartProductEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE cart_product  SET count = count + 1 WHERE product_id = :id AND username = :username")
    Mono<Void> incrementCount(@Param("id") Long id, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE cart_product  SET count = count - 1 WHERE product_id = :id and username = :username AND count > 0")
    Mono<Void> decrementCount(@Param("id") Long id, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM cart_product WHERE product_id = :id and username = :username")
    Mono<Void> setCountZero(@Param("id") Long id, @Param("username")String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM cart_product WHERE product_id = :id AND username = :username")
    Mono<Void> deleteProductFromCart(@Param("id") Long id, @Param("username")String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM cart_product WHERE username = :username")
    Mono<Void> deleteAllFromCart(@Param("username")String username);

    @Query("SELECT * FROM cart_product WHERE product_id = :id AND username = :username")
    Mono<CartProductEntity> findByProductIdAndUsername(@Param("id") Long id, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("insert into cart_product (product_id, username, count) values (:productId,:username, :count);")
    Mono<Void> insertRow(@Param("username") String username, @Param("productId") Long productId, @Param("count") int count);


}
