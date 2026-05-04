package ru.yandex.practicum.mymarket.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.yandex.practicum.mymarket.AbstractTestContainersTest;
import ru.yandex.practicum.mymarket.entity.OrderEntity;

public class OrderServiceTest extends AbstractTestContainersTest {
    @Autowired
    OrderService orderService;

    @Test
    void testCreateOrder(){
        orderService.create();

        List<OrderEntity> orders = orderService.findAll();

        assertEquals(1, orders.size());
    }
}
