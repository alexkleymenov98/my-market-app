package ru.yandex.practicum.mymarket.controller;

import java.util.List;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ru.yandex.practicum.mymarket.entity.OrderEntity;
import ru.yandex.practicum.mymarket.model.OrderDto;
import ru.yandex.practicum.mymarket.model.ProductDto;
import ru.yandex.practicum.mymarket.service.OrderService;

@Controller
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ModelAndView getOrders(){
        ModelAndView modelAndView = new ModelAndView("orders");

        List<OrderEntity> orders = orderService.findAll();

        modelAndView.addObject("orders", orders);
 

        return modelAndView;
    }

    @GetMapping("/orders/{id}")
    public ModelAndView getOrders(@PathVariable Long id, @RequestParam(value = "newOrder", required = false, defaultValue = "false") boolean newOrder){
        ModelAndView modelAndView = new ModelAndView("order");

        OrderEntity order = orderService.findById(id);

        modelAndView.addObject("order", order);
        modelAndView.addObject("newOrder", newOrder);
 

        return modelAndView;
    }

    @PostMapping("/buy")
    public ModelAndView buy(){
        ModelAndView modelAndView = new ModelAndView();

        Long orderId = orderService.create();

        modelAndView.setViewName("redirect:/orders/" + orderId +"?newOrder=true" );
        return modelAndView;
    }
}
