package ru.yandex.practicum.mymarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import ru.yandex.practicum.mymarket.service.PaymentClientService;
import ru.yandex.practicum.mymarket.service.ProductService;
import ru.yandex.practicum.mymarket.utils.ProductUtils;
import ru.yandex.practicum.mymarket.utils.SecurityUtils;

@Controller
public class CartController {

    private final ProductService productService;
    private final PaymentClientService paymentClientService;


    public CartController(ProductService productService, PaymentClientService paymentClientService) {
        this.productService = productService;
        this.paymentClientService = paymentClientService;
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getCart(){


        return SecurityUtils.getCurrentUsername()
                .defaultIfEmpty("anonymous")
                .flatMap(username->productService.getProductFromCart(username).collectList()
                        .flatMap(products -> {
                            Long total = ProductUtils.getProductsTotal(products);

                            Mono<Long> balance = paymentClientService.getBalance();

                            return Mono.just(Rendering.view("cart")
                                    .modelAttribute("items", products)
                                    .modelAttribute("total", total)
                                    .modelAttribute("balance", balance)
                                    .build());

                        }));
    }


    @PostMapping("/cart/items")
    public Mono<Rendering> updateCountProductInDetailt(
        ServerWebExchange exchange
    ){
        return exchange.getFormData()
            .flatMap(formData ->{
                String action = formData.getFirst("action");
                Long id = Long.parseLong(formData.getFirst("id"));

                return productService.updateProductInCart(id, action)
                .then(productService.getProductFromCart("user").collectList());
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
