package ru.yandex.practicum.mymarket.repository;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import ru.yandex.practicum.mymarket.AbstractTestContainersTest;
import ru.yandex.practicum.mymarket.entity.ProductEntity;

@Sql(scripts = "/import.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProductRepositoryTest extends AbstractTestContainersTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    void testFindById(){
        Optional<ProductEntity> product = productRepository.findById(1l);

        assertTrue(product.isPresent());
    }
}
