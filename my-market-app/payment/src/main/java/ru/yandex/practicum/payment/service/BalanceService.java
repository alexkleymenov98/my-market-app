package ru.yandex.practicum.payment.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.payment.repository.BalanceRepository;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public Mono<BalanceResponse> getBalance(String username){

        BalanceResponse balanceResponse = new BalanceResponse();

        balanceResponse.setBalance(0L);

        return balanceRepository.findById(username)
                .flatMap(balance -> {
                    balanceResponse.setBalance(balance.getBalance());

                    return Mono.just(balanceResponse);
                })
                .defaultIfEmpty(balanceResponse)
                .onErrorResume(e->Mono.just(balanceResponse));
    }

    public Mono<PaymentResponse> pay(Mono<PaymentRequest> params){
        PaymentResponse res = new PaymentResponse();

        return params
                .flatMap(paymentRequest -> {
                    Long amount = paymentRequest.getAmount();
                    String username = paymentRequest.getUsername();

                    return balanceRepository.findById(username)
                            .flatMap(balanceEntity->{
                               Long currentBalance = balanceEntity.getBalance();
                               PaymentResponse response = new PaymentResponse();

                               if(currentBalance >= amount){

                                   balanceEntity.setBalance(currentBalance - amount);

                                   return balanceRepository.save(balanceEntity)
                                           .map(updateBalance -> {
                                               response.setSuccess(true);

                                               response.setRemainingBalance(updateBalance.getBalance());

                                               return response;
                                           });

                               }else {
                                   response.setSuccess(false);
                                   response.setRemainingBalance(currentBalance);

                               }
                                return Mono.just(response);
                            });

                })
                .onErrorResume(e->Mono.just(res));
    }
}
