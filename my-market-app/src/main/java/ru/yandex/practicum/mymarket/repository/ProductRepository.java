package ru.yandex.practicum.mymarket.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.mymarket.entity.ProductEntity;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long>{
    ProductEntity findById(Long id);
    List<ProductEntity> findByCountGreaterThan(int count);
    Page<ProductEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
