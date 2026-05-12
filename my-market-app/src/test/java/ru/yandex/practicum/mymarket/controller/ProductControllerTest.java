package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.ProductImportService;
import ru.yandex.practicum.mymarket.service.ProductService;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;

    @MockitoBean  
    private ProductImportService productImportService;

    @Test
    void getItem_returns200() {
        ProductEntity mockProduct = new ProductEntity(1L, "apple", "Новый телефон", "/assets/image.png", 19999L, 12);
        
        when(productService.findById(1L)).thenReturn(Mono.just(mockProduct));

        webTestClient.get()
                .uri("/items/1")
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