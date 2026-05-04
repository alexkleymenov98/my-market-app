package ru.yandex.practicum.mymarket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Mono<ProductEntity> findById(Long id){
        return productRepository.findById(id);
    }

    public Flux<ProductEntity> getProductFromCart(){
        return productRepository.findByCountGreaterThan(0);
    }

    // public Mono<Page<ProductEntity>> findAll(int pageNumber, int pageSize, String sort, String search){
    //     Pageable pageable;

    //     int pageNumerIndex = Math.max(0, pageNumber - 1);

    //     if(sort.equals("ALPHA") || sort.equals("PRICE")){
    //         Sort sortObj;

    //         if("ALPHA".equals(sort)){
    //             sortObj = Sort.by(Sort.Direction.DESC, "title" );
    //         } else {
    //             sortObj = Sort.by(Sort.Direction.DESC, "price" );
    //         }

    //         pageable = PageRequest.of(pageNumerIndex, pageSize, sortObj);
    //     } else {
    //         pageable = PageRequest.of(pageNumerIndex, pageSize);
    //     }

    //     if (search != null && !search.trim().isEmpty()) {
    //         String searchTerm = search.trim();
    //         return productRepository.findByTitleContainingIgnoreCase(searchTerm, pageable);
    //     }

    //     return productRepository.count().zipWith(productRepository.findAll(pageable));
    // }

    public Mono<Void> updateProductInCart(Long id, String action){
        if("PLUS".equals(action)){
            return productRepository.incrementCount(id);
        }

        if("MINUS".equals(action)){
            return productRepository.decrementCount(id);
        }

        if("DELETE".equals(action)){
            return productRepository.setCountZero(id);
        }

        return Mono.empty();
    }
}
