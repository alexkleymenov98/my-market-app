package ru.yandex.practicum.mymarket.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.OrderEntity;
import ru.yandex.practicum.mymarket.service.OrderService;

@Controller
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public Mono<Rendering> getOrders(){ 
        return orderService.findAll()
        .collectList()
        .map(orderList -> Rendering.view("orders").modelAttribute("orders", orderList).build());
    }


    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrders(@PathVariable Long id, @RequestParam(value = "newOrder", required = false, defaultValue = "false") boolean newOrder){

        Mono<OrderEntity> order = orderService.findById(id);
 
        return Mono.just(Rendering.view("order")
                .modelAttribute("order", order)
                .modelAttribute("newOrder", newOrder)
                .build());
    }

    // @PostMapping("/buy")
    // public ModelAndView buy(){
    //     ModelAndView modelAndView = new ModelAndView();

    //     Long orderId = orderService.create();

    //     modelAndView.setViewName("redirect:/orders/" + orderId +"?newOrder=true" );
    //     return modelAndView;
    // }
}
