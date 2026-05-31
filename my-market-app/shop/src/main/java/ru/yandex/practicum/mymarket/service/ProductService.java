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
import ru.yandex.practicum.mymarket.entity.CartProductEntity;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.CartProductRepository;
import ru.yandex.practicum.mymarket.repository.ProductRepository;
import ru.yandex.practicum.mymarket.utils.SecurityUtils;


@Service
public class ProductService {
    private static final String CACHE_NAME = "products";
    private static final String CACHE_NAME_CART = "products_cart";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

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

    public ProductService(ProductRepository productRepository, ReactiveRedisTemplate<String, ProductEntity> redisTemplate, CartProductRepository cartProductRepository) {
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
        this.redisTemplate = redisTemplate;
    }

    public Mono<ProductEntity> findById(Long id, String username){
        String cacheKey = CACHE_NAME + "_" + username + "_" + id ;
        return redisTemplate.opsForValue()
                .get(cacheKey)
                .switchIfEmpty(Mono.defer(() -> databaseClient.sql(String.format("""
                    SELECT
                    	p.id,
                        p.title,
                        p.description,
                        p.img_path,
                        p.price,
                    	coalesce(cp.count ,0) as count
                    from products p
                    left join cart_product cp on p.id  = cp.product_id and cp.username  = :username
                    where p.id  = :id
                    """))
                        .bind("id", id)
                        .bind("username", username)
                        .map((row, metadata) -> new ProductEntity(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("description", String.class),
                                row.get("img_path", String.class),
                                row.get("price", Long.class),
                                row.get("count", Integer.class)
                        ))
                        .one()
                        .flatMap(product -> redisTemplate.opsForValue()
                                .set(cacheKey, product, CACHE_TTL)
                                .thenReturn(product))));
    }

    public Flux<ProductEntity> getProductFromCart(String username) {
        String CURRENT_CACHE_NAME_CART = CACHE_NAME_CART + username;

        return redisTemplate.opsForList().range(CURRENT_CACHE_NAME_CART, 0, -1)
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.defer(() ->
                        databaseClient.sql("""
                    SELECT
                        p.id,
                        p.title,
                        p.description,
                        p.img_path,
                        p.price,
                        COALESCE(cp.count, 0) as count
                    FROM products p
                    LEFT JOIN cart_product cp ON p.id = cp.product_id AND cp.username = :username
                    WHERE COALESCE(cp.count, 0) > 0
                    """)
                                .bind("username", username)
                                .fetch()
                                .all()
                                .map(this::mapToProduct)
                                .collectList()
                                .flatMapMany(products -> {
                                    // ✅ Проверка: сохраняем в Redis только если список не пустой
                                    if (products == null || products.isEmpty()) {
                                        // Если корзина пуста, удаляем ключ из Redis если он существует
                                        return redisTemplate.delete(CURRENT_CACHE_NAME_CART)
                                                .thenMany(Flux.empty());
                                    }

                                    // Сохраняем в Redis
                                    return redisTemplate.opsForList()
                                            .leftPushAll(CURRENT_CACHE_NAME_CART, products.toArray(new ProductEntity[0]))
                                            .then(redisTemplate.expire(CURRENT_CACHE_NAME_CART, CACHE_TTL))
                                            .thenMany(Flux.fromIterable(products));
                                })
                ));
    }

    public Mono<Page<ProductEntity>> findAll(int pageNumber, int pageSize, String sort, String search, String username){
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
            SELECT 
                p.id,
                p.title,
                p.description,
                p.img_path,
                p.price,
                coalesce(cp.count, 0) as count
            FROM products p
            LEFT JOIN cart_product cp ON p.id = cp.product_id AND cp.username = :username
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
                 .bind("username", username)
            .fetch()
            .all()
            .map(this::mapToProduct)
            .collectList();
        

        return productsMono.zipWith(countMono)
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    public Mono<Void> updateProductInCart(Long id, String action){
        return Mono.defer(() -> SecurityUtils.getCurrentUsername()
                .flatMap(username -> {
                    String userCartKey = CACHE_NAME_CART + username;
                    String productKey = CACHE_NAME + "_" + username + "_" + id; // если есть

                    return cartProductRepository.findByProductIdAndUsername(id, username).
                            hasElement()
                            .flatMap(exists->{
                                Mono<Void> operation;
                                if ("PLUS".equals(action)) {
                                    if(exists){
                                        operation = cartProductRepository.incrementCount(id, username);
                                    }else {

                                        operation = cartProductRepository.insertRow(username, id, 1).then();
//
                                    }
                                } else if ("MINUS".equals(action)) {
                                    operation = cartProductRepository.decrementCount(id, username);
                                } else if ("DELETE".equals(action)) {
                                    operation = cartProductRepository.deleteProductFromCart(id, username);
                                } else {
                                    operation = Mono.empty();
                                }

                                // Удаляем несколько ключей
                                return operation
                                        .then(redisTemplate.delete(userCartKey))
                                        .then(redisTemplate.delete(productKey))
                                        .then();
                            });
                }));
    }
}
