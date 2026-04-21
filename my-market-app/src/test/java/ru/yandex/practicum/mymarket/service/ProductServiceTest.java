package ru.yandex.practicum.mymarket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.yandex.practicum.mymarket.AbstractTestContainersTest;
import ru.yandex.practicum.mymarket.entity.ProductEntity;

public class ProductServiceTest extends AbstractTestContainersTest{
    @Autowired
    ProductService productService;

    @Test
    void testFindById(){
        Optional<ProductEntity> product = productService.findById(1l);

        assertTrue(product.isPresent());
    }

    @Test
    void testIncrementCount(){
        productService.updateProductInCart(1l, "PLUS");

        Optional<ProductEntity> product = productService.findById(1l);

        assertEquals(product.get().getCount(), 11);
    }


    @Test
    void testDecrementCount(){
        productService.updateProductInCart(1l, "MINUS");

        Optional<ProductEntity> product = productService.findById(1l);

        assertEquals(product.get().getCount(), 9);
    }

    @Test
    void testSetCountZero(){
        productService.updateProductInCart(1l, "DELETE");

        Optional<ProductEntity> product = productService.findById(1l);

        assertEquals(product.get().getCount(), 0);
    }

}

