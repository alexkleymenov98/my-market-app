package ru.yandex.practicum.mymarket.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import ru.yandex.practicum.mymarket.AbstractTestContainersTest;
import ru.yandex.practicum.mymarket.entity.ProductEntity;


public class ProductRepositoryTest extends AbstractTestContainersTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    void testFindById(){
        Optional<ProductEntity> product = productRepository.findById(1l);

        assertTrue(product.isPresent());
    }

    @Test
    void testIncrementCount(){
        productRepository.incrementCount(1l);

        Optional<ProductEntity> product = productRepository.findById(1l);

        assertEquals(product.get().getCount(), 11);
    }


    @Test
    void testDecrementCount(){
        productRepository.decrementCount(1l);

        Optional<ProductEntity> product = productRepository.findById(1l);

        assertEquals(product.get().getCount(), 9);
    }

    @Test
    void testSetCountZero(){
        productRepository.setCountZero(1l);

        Optional<ProductEntity> product = productRepository.findById(1l);

        assertEquals(product.get().getCount(), 0);
    }

    @Test
    void testSetCountZeroAll(){
        productRepository.deleteAllFromCart();

        Optional<ProductEntity> product = productRepository.findById(1l);

        assertEquals(product.get().getCount(), 0);
    }
}
