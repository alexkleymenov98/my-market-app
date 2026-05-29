package ru.yandex.practicum.mymarket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.payment.api.PaymentApi;
import ru.yandex.practicum.mymarket.payment.model.BalanceResponse;
import ru.yandex.practicum.mymarket.payment.model.PaymentRequest;
import ru.yandex.practicum.mymarket.payment.model.PaymentResponse;

@Service
public class PaymentClientService {
    private final PaymentApi paymentApi;
    private static final Logger log = LoggerFactory.getLogger(PaymentClientService.class);

    public PaymentClientService(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Mono<Long> getBalance(String username){

        log.debug("GET BALANCE --- START");

        return paymentApi.getBalance(username)
                .doOnSubscribe(subscription ->
                        log.debug("Request subscribed to Payment Service")
                )
                .doOnRequest(request ->
                        log.debug("Request sent, waiting for response...")
                )
                .map(BalanceResponse::getBalance)
                .doOnNext(System.out::println)
                .onErrorResume(e -> Mono.just(0L));
    }

    public Mono<Boolean> pay(PaymentRequest request){
        return paymentApi.pay(request)
                .map(PaymentResponse::getSuccess)
                .onErrorResume(e->Mono.just(false));
    }
}
