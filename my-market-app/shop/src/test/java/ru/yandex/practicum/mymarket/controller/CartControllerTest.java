package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.test.context.support.WithMockUser;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.PaymentClientService;
import ru.yandex.practicum.mymarket.service.ProductService;

import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
@ActiveProfiles("test")
public class CartControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean 
    private ProductService productService;

    @MockitoBean
    private PaymentClientService paymentClientService;


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getСart_returns200() {
        ProductEntity mockProduct = new ProductEntity(1L, "apple", "Новый телефон", "/assets/image.png", 19999L, 12);


        when(paymentClientService.getBalance("user")).thenReturn(Mono.just(0L));
        when(productService.getProductFromCart("user")).thenReturn(Flux.just(mockProduct));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus()
                .isOk()
                 .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("apple");
                    assert html.contains("Новый телефон");
                });;
    }
}
