package ru.yandex.practicum.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.api.PrivateApi;
import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.payment.service.BalanceService;

@RestController
public class PaymentController implements PrivateApi {

    private final BalanceService balanceService;

    public PaymentController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(
        @Parameter(hidden = true) final ServerWebExchange exchange
    ){
        return balanceService.getBalance()
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> pay(
         @Parameter(name = "PaymentRequest", description = "", required = true) @Valid @RequestBody Mono<PaymentRequest> paymentRequest,
        @Parameter(hidden = true) final ServerWebExchange exchange
    ){
        return balanceService.pay(paymentRequest).map(ResponseEntity::ok);
    }
}
