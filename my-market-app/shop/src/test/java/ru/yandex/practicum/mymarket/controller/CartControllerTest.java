package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.test.context.support.WithMockUser;
import ru.yandex.practicum.mymarket.config.SecureConfig;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.PaymentClientService;
import ru.yandex.practicum.mymarket.service.ProductService;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CartController.class)
@Import(SecureConfig.class)
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
    void getCart_returns200() {
        ProductEntity mockProduct = new ProductEntity(1L, "apple", "Новый телефон", "/assets/image.png", 19999L, 12);


        when(paymentClientService.getBalance("user")).thenReturn(Mono.just(1000L));
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
                    assert html.contains("Недостаточно средств на счете. Требуется:");
                });;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCart_returns200WithPay() {
        ProductEntity mockProduct = new ProductEntity(1L, "apple", "Новый телефон", "/assets/image.png", 19L, 12);


        when(paymentClientService.getBalance("user")).thenReturn(Mono.just(1000L));
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
                    assert html.contains("Купить");
                });;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCart_returns200WithoutPayment() {
        ProductEntity mockProduct = new ProductEntity(1L, "apple", "Новый телефон", "/assets/image.png", 19999L, 12);


        when(paymentClientService.getBalance("user")).thenReturn(Mono.just(-1L));
        when(productService.getProductFromCart("user")).thenReturn(Flux.just(mockProduct));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Сервис оплаты недоступен");
                    assert html.contains("Новый телефон");
                });;
    }

    @Test
    void shouldReturn401_WhenNoTokenProvided_GetCart() {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isFound()  // 302 Found
                .expectHeader().valueMatches("Location", ".*/login.*");
    }

}
