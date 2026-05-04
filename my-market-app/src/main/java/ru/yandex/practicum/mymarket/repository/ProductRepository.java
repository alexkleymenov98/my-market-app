package ru.yandex.practicum.mymarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.mymarket.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> findByCountGreaterThan(int count);
    Page<ProductEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.count = p.count + 1 WHERE p.id = :id")
    void incrementCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.count = p.count - 1 WHERE p.id = :id AND p.count > 0")
    void decrementCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.count = 0 WHERE p.id = :id")
    void setCountZero(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.count = 0 WHERE p.count > 0")
    void deleteAllFromCart();
}
