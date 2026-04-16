package ru.yandex.practicum.mymarket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.yandex.practicum.mymarket.entity.OrderEntity;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.repository.ProductRepository;
import ru.yandex.practicum.mymarket.utils.ProductUtils;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Long create(){
        OrderEntity order = new OrderEntity();

        List<ProductEntity> products = productRepository.findByCountGreaterThan(0);

        products.forEach(product -> order.addProduct(product));
        order.setTotalSum(ProductUtils.getProductsTotal(products));

        Long newOrderId =  orderRepository.save(order).getId();

        productRepository.deleteAllFromCart();

        return newOrderId;
    
    }

    public List<OrderEntity> findAll(){
        return orderRepository.findAll();
    }

    public OrderEntity findById(Long id){
        return orderRepository.findById(id).orElse(null);
    }
}
