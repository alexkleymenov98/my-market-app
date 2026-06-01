package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
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

    // Тестовая конфигурация безопасности (упрощенная, без OAuth2)
    @Configuration
    static class TestSecurityConfig {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/login", "/", "/items/**").permitAll()
                            .anyExchange().authenticated()
                    )
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                    .build();
        }

        @Bean
        public MapReactiveUserDetailsService userDetailsService() {
            UserDetails user = User.builder()
                    .username("user")
                    .password("{noop}password")
                    .roles("USER")
                    .build();
            return new MapReactiveUserDetailsService(user);
        }
    }
}
