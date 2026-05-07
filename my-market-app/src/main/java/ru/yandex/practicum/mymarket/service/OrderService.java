package ru.yandex.practicum.mymarket.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.OrderEntity;
import ru.yandex.practicum.mymarket.entity.OrderProductEntity;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.OrderProductRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.repository.ProductRepository;
import ru.yandex.practicum.mymarket.utils.ProductUtils;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderProductRepository orderProductRepository){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Transactional
    public Mono<Long> create() {
        OrderEntity order = new OrderEntity();
    
    return productRepository.findByCountGreaterThan(0)
        .collectList()
        .flatMap(products -> {
            // Рассчитываем общую сумму
            long totalSum = products.stream()
                .mapToLong(ProductEntity::getPrice)
                .sum();
            
            order.setTotalSum(totalSum);
            
            // Сохраняем заказ
            return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    // Создаем связи заказ-продукт
                    Flux<OrderProductEntity> orderProducts = Flux.fromIterable(products)
                        .map(product -> {
                            OrderProductEntity op = new OrderProductEntity();
                            op.setOrderId(savedOrder.getId());
                            op.setProductId(product.getId());
                            op.setCount(product.getCount()); // или другое количество
                            return op;
                        });
                    
                    // Сохраняем все связи
                    return orderProductRepository.saveAll(orderProducts)
                        .then(productRepository.deleteAllFromCart())
                        .thenReturn(savedOrder.getId());
                });
        });
    }

    public Flux<OrderEntity> findAll(){
        return orderRepository.findAll();
    }

    public Mono<OrderEntity> findById(Long id){
        
        return orderRepository.findById(id)
            .flatMap(order -> 
                orderProductRepository.findByOrderId(order.getId())
                    .collectList()
                    .flatMap(orderProducts -> {
                        Map<Long, Integer> countMap = orderProducts.stream()
                        .collect(Collectors.toMap(
                            OrderProductEntity::getProductId,
                            OrderProductEntity::getCount
                        ));
                    
                    List<Long> productIds = new ArrayList<>(countMap.keySet());
                    
                    
                    return productRepository.findAllById(productIds)
                        .collectList()
                        .map(products -> {
                            List<ProductEntity> productsWithCount = products.stream()
                                .map(product -> {
                                    product.setCount(countMap.get(product.getId()));
                                    return product;
                                })
                                .collect(Collectors.toList());
                            
                            order.setItems(productsWithCount);
                            return order;
                        });
                })
        );
}
}
