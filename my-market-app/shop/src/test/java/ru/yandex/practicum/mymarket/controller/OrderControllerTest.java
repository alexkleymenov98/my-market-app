package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.config.SecureConfig;

import ru.yandex.practicum.mymarket.entity.OrderEntity;
import ru.yandex.practicum.mymarket.service.OrderService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@WebFluxTest(controllers = OrderController.class)
@Import(SecureConfig.class)
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;


    @Test
    void shouldReturn302_WhenNoTokenProvided() {
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches("Location", ".*/login.*");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getOrders_returns200() {
        // Создаем тестовые данные
        OrderEntity order1 = new OrderEntity();
        order1.setId(1L);
        order1.setUsername("user");
        order1.setTotalSum(1000L);

        when(orderService.findById(anyLong())).thenReturn(Mono.just(order1));

        webTestClient.get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML);
    }

}
