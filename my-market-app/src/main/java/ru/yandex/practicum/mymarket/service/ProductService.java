package ru.yandex.practicum.mymarket.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

     @Autowired
    private DatabaseClient databaseClient;

    private ProductEntity mapToProduct(Map<String, Object> row) {
        ProductEntity product = new ProductEntity();
        product.setId((Long) row.get("id"));
        product.setTitle((String) row.get("title"));
        product.setDescription((String) row.get("description"));
        product.setImgPath((String) row.get("img_path"));
        product.setPrice((Long) row.get("price"));
        product.setCount((Integer) row.get("count"));
        return product;
    }

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Mono<ProductEntity> findById(Long id){
        return productRepository.findById(id);
    }

    public Flux<ProductEntity> getProductFromCart(){
        return productRepository.findByCountGreaterThan(0);
    }

    public Mono<Page<ProductEntity>> findAll(int pageNumber, int pageSize, String sort, String search){
        Pageable pageable;

        int pageNumerIndex = Math.max(0, pageNumber - 1);

        Sort sortObj;
        String sortColumn = "id";

        if(sort.equals("ALPHA") || sort.equals("PRICE")){

            if("ALPHA".equals(sort)){
                sortObj = Sort.by(Sort.Direction.DESC, "title" );
                sortColumn = "title";
            } else {
                sortObj = Sort.by(Sort.Direction.DESC, "price" );
                sortColumn = "price";
            }

            pageable = PageRequest.of(pageNumerIndex, pageSize, sortObj);
        } else {
            pageable = PageRequest.of(pageNumerIndex, pageSize);

        }

        Mono<Long> countMono = productRepository.count();

        int offset = pageNumerIndex * pageSize;

         Mono<List<ProductEntity>> productsMono = databaseClient.sql(String.format("""
            SELECT * FROM products
            WHERE (:search IS NULL OR :search = '' OR 
            LOWER(title) LIKE LOWER(CONCAT('%%', :search, '%%')) OR
            LOWER(description) LIKE LOWER(CONCAT('%%', :search, '%%')))
            ORDER BY %s
            LIMIT :limit
            OFFSET :offset
                 """
         , sortColumn))
            .bind("limit", pageSize)
            .bind("offset", offset)
            .bind("search", search)
            // .bind("sort", sortColumn)
            .fetch()
            .all()
            .map(this::mapToProduct)
            .collectList();
        

        return productsMono.zipWith(countMono)
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

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
