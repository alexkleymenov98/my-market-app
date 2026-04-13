package ru.yandex.practicum.mymarket.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import ru.yandex.practicum.mymarket.model.OrderDto;
import ru.yandex.practicum.mymarket.model.ProductDto;

@Controller
public class OrderController {

    @GetMapping("/orders")
    public ModelAndView getOrders(){
        ModelAndView modelAndView = new ModelAndView("orders");

        ProductDto item = new ProductDto(1L, "Продукт", "Мой продукт", "https://pfc-cska.com/uploads/content/placeholders/og.png", 100L, 100);

        OrderDto order1 = new OrderDto(1L, List.of(item));
        OrderDto order2 = new OrderDto(2L, List.of(item));


        modelAndView.addObject("orders", List.of(order1, order2));
 

        return modelAndView;
    }

    @GetMapping("/orders/{id}")
    public ModelAndView getOrders(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("order");

        ProductDto item = new ProductDto(1L, "Продукт", "Мой продукт", "https://pfc-cska.com/uploads/content/placeholders/og.png", 100L, 100);

        OrderDto order1 = new OrderDto(1L, List.of(item));

        modelAndView.addObject("order", order1);
 

        return modelAndView;
    }
}
