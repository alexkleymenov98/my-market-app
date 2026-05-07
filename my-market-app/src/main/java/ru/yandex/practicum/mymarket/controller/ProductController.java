package ru.yandex.practicum.mymarket.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.ProductImportService;
import ru.yandex.practicum.mymarket.service.ProductService;
import ru.yandex.practicum.mymarket.utils.ProductUtils;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    public ProductController(ProductService productService, ProductImportService productImportService){
        this.productService = productService;
        this.productImportService = productImportService;
    }

    // @GetMapping(value = {"/items", "/"})
    // public ModelAndView getProducts(
    //     @RequestParam(value = "search", required = false, defaultValue = "") String search,
    //     @RequestParam(value = "sort", required = false, defaultValue = "NO") String sort,
    //     @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
    //     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
    //     @RequestParam(value = "importSuccess", required = false, defaultValue = "false") boolean importSuccess
    // )
    //     {

    //     ModelAndView modelAndView = new ModelAndView("items");

    //     Page<ProductEntity> page = productService.findAll(pageNumber, pageSize, sort, search);

    //     modelAndView.addObject("items", ProductUtils.mapToRowProducts(page.getContent(), 3));
    //     modelAndView.addObject("paging", page);
    //     modelAndView.addObject("sort", sort);
    //     modelAndView.addObject("search", search);
    //     modelAndView.addObject("importSuccess", importSuccess);

    //     return modelAndView;
    // }

    //  @GetMapping(value = {"/items", "/"})
    // public Mono<Rendering> getProducts(
    //     ServerWebExchange exchange
    // )
    //     {
    //         return exchange.getFormData()
    //         .map(formData ->{
    //             Map<String, Object> result = new HashMap<>();
    //             String search = Optional.ofNullable(formData.getFirst("search"))
    //                 .map(Object::toString)
    //                 .orElse("");;

    //             String sort = Optional.ofNullable(formData.getFirst("sort"))
    //                 .map(Object::toString)
    //                 .orElse("NO");;

    //             int pageNumber = Optional.ofNullable(formData.getFirst("pageNumber"))
    //                 .map(Object::toString)
    //                 .map(Integer::parseInt)
    //                 .orElse(1);
                
    //             int pageSize = Optional.ofNullable(formData.getFirst("pageSize"))
    //                 .map(Object::toString)
    //                 .map(Integer::parseInt)
    //                 .orElse(1);
                
    //             boolean importSuccess = Optional.ofNullable(formData.getFirst("importSuccess"))
    //                 .map(Object::toString)
    //                 .map(Boolean::parseBoolean)
    //                 .orElse(false);

    //                 result.put("search", search);
    //                 result.put("sort", exchange);
    //                 result.put("pageNumber", pageNumber);
    //                 result.put("pageSize", pageSize);
    //                 result.put("importSucess", importSuccess);

    //             return result;
    //         })
    //         .map(formData -> {
                
    //         })

    // }

    // @PostMapping("/items")
    // public ModelAndView updateCountProductInList(
    //     @RequestParam(value = "id", required = true) Long id,
    //     @RequestParam(value = "action", required = true) String action,
    //     @RequestParam(value = "search", required = false, defaultValue = "") String search,
    //     @RequestParam(value = "sort", required = false, defaultValue = "NO") String sort,
    //     @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
    //     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    // ){

    //     productService.updateProductInCart(id, action);

    //     ModelAndView modelAndView = new ModelAndView();
    //     modelAndView.setViewName("redirect:/items?search=" + search + "&sort="+sort+"&pageNumber="+pageNumber+"&pageSize="+pageSize );
    //     return modelAndView;
    // }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getProductDetail(@PathVariable Long id){
        Mono<ProductEntity> item = productService.findById(id);

        return Mono.just(Rendering.view("item")
                .modelAttribute("item", item)
                .build());
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> updateCountProductInDetailt(
        @PathVariable(value = "id", required = true) Long id,
        ServerWebExchange exchange
    ){

   
        return exchange.getFormData()
            .map(formData ->{
                String action = formData.getFirst("action");

                return action;
            })
            .flatMap(action ->productService
            .updateProductInCart(id, action))
            .then(productService.findById(id).map(item -> 
                Rendering.view("item")
                .modelAttribute("item", item)
                .build()));
    }

    // @PostMapping("/upload")
    // public ModelAndView uploadProducts(@RequestParam("file") MultipartFile file){


    //     Boolean importSuccess = false;

    //     try {
    //         productImportService.uploadProductsFromXlsx(file);
    //         importSuccess = true;
    //     } catch(Exception e){
    //         importSuccess = false;
    //      }

    //     ModelAndView modelAndView = new ModelAndView();
    //     modelAndView.setViewName("redirect:/items?importSuccess=" + importSuccess);
    //     return modelAndView;
    // }
}
