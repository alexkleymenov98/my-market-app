package ru.yandex.practicum.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.config.SecurityConfig;
import ru.yandex.practicum.payment.controller.PaymentController;
import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.payment.service.BalanceService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(controllers = PaymentController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private BalanceService balanceService;


    @Test
    void shouldReturn401_WhenNoTokenProvided_GetBalance() {
        webClient.get().uri("/private/balance/user3")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getBalance_returns200() {
        BalanceResponse balance = new BalanceResponse();
        balance.setBalance(1000L);
        when(balanceService.getBalance("user1")).thenReturn(Mono.just(balance));

        webClient.mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("SERVICE")))
                .get().uri("/private/balance/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(1000);
    }

    @Test
    void shouldReturn401_WhenNoTokenProvided_Pay() {
        PaymentRequest request = new PaymentRequest();
        request.setUsername("user3");
        request.setAmount(1000L);

        webClient.post().uri("/private/pay")
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "user3", roles = "USER")
    void shouldReturn200_WhenPayWithValidData() {
        // Подготовка запроса
        PaymentRequest request = new PaymentRequest();
        request.setUsername("user3");
        request.setAmount(1000L);

        // Подготовка ответа
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setRemainingBalance(9000L);

        // Мокаем сервис
        when(balanceService.pay(any(Mono.class)))
                .thenReturn(Mono.just(response));

        // Выполняем запрос
        webClient.post()
                .uri("/private/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .value(res -> {
                    assert res.getSuccess().equals(true);
                    assert res.getRemainingBalance() == 9000L;
                });
    }

    @Test
    @WithMockUser(username = "user3", roles = "USER")
    void shouldReturn200_WhenPayWithNoValidData() {
        // Подготовка запроса
        PaymentRequest request = new PaymentRequest();
        request.setUsername("user3");
        request.setAmount(10000L);

        // Подготовка ответа
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setRemainingBalance(9000L);

        when(balanceService.pay(any(Mono.class)))
                .thenReturn(Mono.just(response));

        webClient.post()
                .uri("/private/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .value(res -> {
                    assert res.getSuccess().equals(false);
                    assert res.getRemainingBalance() == 9000L;
                });
    }

}