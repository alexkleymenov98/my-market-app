package ru.yandex.practicum.mymarket.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ru.yandex.practicum.mymarket.entity.ProductEntity;

import ru.yandex.practicum.mymarket.service.ProductService;
import ru.yandex.practicum.mymarket.utils.ProductUtils;

@Controller
public class CartController {

    private final ProductService productService;


    public CartController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/cart/items")
    public ModelAndView getCart(){
    
        ModelAndView modelAndView = new ModelAndView("cart");

        List<ProductEntity> products = productService.getProductFromCart();

        Long total = ProductUtils.getProductsTotal(products);

        modelAndView.addObject("items", products);
        modelAndView.addObject("total", total);

        return modelAndView;
    }
}
