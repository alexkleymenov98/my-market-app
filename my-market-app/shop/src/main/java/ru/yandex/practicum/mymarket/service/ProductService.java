package ru.yandex.practicum.mymarket.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.ProductRepository;


@Service
public class ProductService {
    private static final String CACHE_NAME = "products";
    private static final String CACHE_NAME_CART = "products_cart";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository productRepository;

    private final ReactiveRedisTemplate<String, ProductEntity> redisTemplate;

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

    public ProductService(ProductRepository productRepository, ReactiveRedisTemplate<String, ProductEntity> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    public Mono<ProductEntity> findById(Long id){
        String cacheKey = CACHE_NAME + id;
        return redisTemplate.opsForValue()
                .get(cacheKey)
                .switchIfEmpty(Mono.defer(() -> productRepository.findById(id)
                        .flatMap(product -> redisTemplate.opsForValue()
                                .set(cacheKey, product, CACHE_TTL)
                                .thenReturn(product))));
    }

    public Flux<ProductEntity> getProductFromCart(){
        return redisTemplate.opsForList().range(CACHE_NAME_CART, 0, -1)
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.defer(()->productRepository.findByCountGreaterThan(0)
                        .collectList()
                        .flatMapMany(products ->
                                redisTemplate.opsForList()
                                        .leftPushAll(CACHE_NAME_CART, products)
                                        .then(redisTemplate.expire(CACHE_NAME_CART, CACHE_TTL))
                                        .thenMany(Flux.fromIterable(products)))));
    }

    public Mono<Page<ProductEntity>> findAll(int pageNumber, int pageSize, String sort, String search){
        Pageable pageable;

        int pageNumberIndex = Math.max(0, pageNumber - 1);

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

            pageable = PageRequest.of(pageNumberIndex, pageSize, sortObj);
        } else {
            pageable = PageRequest.of(pageNumberIndex, pageSize);

        }

        Mono<Long> countMono = productRepository.count();

        int offset = pageNumberIndex * pageSize;

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
            .fetch()
            .all()
            .map(this::mapToProduct)
            .collectList();
        

        return productsMono.zipWith(countMono)
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    public Mono<Void> updateProductInCart(Long id, String action){

       return  redisTemplate.delete(CACHE_NAME_CART)
                .then(Mono.defer(() -> {
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
                }));

    }
}
