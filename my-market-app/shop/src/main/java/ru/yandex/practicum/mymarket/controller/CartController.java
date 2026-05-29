package ru.yandex.practicum.mymarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.PaymentClientService;
import ru.yandex.practicum.mymarket.service.ProductService;
import ru.yandex.practicum.mymarket.utils.ProductUtils;
import ru.yandex.practicum.mymarket.utils.SecurityUtils;

import java.util.List;

@Controller
public class CartController {

    private final ProductService productService;
    private final PaymentClientService paymentClientService;


    public CartController(ProductService productService, PaymentClientService paymentClientService) {
        this.productService = productService;
        this.paymentClientService = paymentClientService;
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getCart() {
        return SecurityUtils.getCurrentUsername()
                .defaultIfEmpty("anonymous")
                .flatMap(username ->
                        productService.getProductFromCart(username).collectList()
                                .flatMap(products -> {
                                    Long total = ProductUtils.getProductsTotal(products);
                                    Mono<Long> balanceMono = paymentClientService.getBalance(username);

                                    // Ждем оба значения: products и balance
                                    return Mono.zip(Mono.just(products), balanceMono)
                                            .map(tuple -> {
                                                List<ProductEntity> productsList = tuple.getT1();
                                                Long balanceValue = tuple.getT2();

                                                return Rendering.view("cart")
                                                        .modelAttribute("items", productsList)
                                                        .modelAttribute("total", total)
                                                        .modelAttribute("balance", balanceValue)
                                                        .build();
                                            });
                                })
                );
    }


    @PostMapping("/cart/items")
    public Mono<Rendering> updateCountProductInDetailt(
        ServerWebExchange exchange
    ){
        return exchange.getFormData()
            .flatMap(formData ->{
                String action = formData.getFirst("action");
                Long id = Long.parseLong(formData.getFirst("id"));

                return SecurityUtils.getCurrentUsername()
                        .defaultIfEmpty("anonymous")
                        .flatMap(username->productService.updateProductInCart(id, action)
                                .then(productService.getProductFromCart("user").collectList()));
            })
            .flatMap(products -> {
                Long total = ProductUtils.getProductsTotal(products);

                return Mono.just(Rendering.view("cart")
                    .modelAttribute("items", products)
                    .modelAttribute("total", total)
                    .build());

        });
    }
}
