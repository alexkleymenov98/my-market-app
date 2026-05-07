package ru.yandex.practicum.mymarket.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.service.ProductImportService;
import ru.yandex.practicum.mymarket.service.ProductService;
import ru.yandex.practicum.mymarket.utils.ProductUtils;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    public ProductController(ProductService productService, ProductImportService productImportService){
        this.productService = productService;
        this.productImportService = productImportService;
    }

     @GetMapping(value = {"/items", "/"})
    public Mono<Rendering> getProducts(
        ServerWebExchange exchange
        
    )
        {
           MultiValueMap<String, String>  queryParams = exchange.getRequest().getQueryParams();

            final String search = queryParams.getFirst("search") != null ? queryParams.getFirst("search") : "";
            final String sort = queryParams.getFirst("sort") != null ? queryParams.getFirst("sort") : "NO";
    
            int pageNumber = 1;
            
            String pageNumberStr = queryParams.getFirst("pageNumber");
            
            if (pageNumberStr != null) {
                try {
                    pageNumber = Integer.parseInt(pageNumberStr);
                } catch (NumberFormatException e) {}
            }
    
            int pageSize = 10;
            String pageSizeStr = queryParams.getFirst("pageSize");
            if (pageSizeStr != null) {
                try {
                    pageSize = Integer.parseInt(pageSizeStr);
                } catch (NumberFormatException e) {}
            }

            final boolean importSuccess = Optional.ofNullable(queryParams.getFirst("importSuccess"))
                                            .map(Boolean::parseBoolean)
                                            .orElse(false);
           

            return productService.findAll(pageNumber, pageSize, sort, search)
                        .map(page ->Rendering.view("items")
                        .modelAttribute("items", ProductUtils.mapToRowProducts(page.getContent(), 3))
                        .modelAttribute("paging", page)
                        .modelAttribute("sort", sort)
                        .modelAttribute("search", search)
                        .modelAttribute("importSuccess", importSuccess)
                        .build());

    }

    @PostMapping("/items")
    public Mono<Rendering> updateCountProductInList(
        ServerWebExchange exchange
    ){

        return exchange.getFormData()
            .flatMap(formData ->{

                Long id = Long.parseLong(formData.getFirst("id"));
                String action = formData.getFirst("action");

                String search = formData.getFirst("search") != null ? formData.getFirst("search") : "";
                String sort = formData.getFirst("sort") != null ? formData.getFirst("sort") : "NO";

                int pageSize = formData.getFirst("pageSize") != null ? Integer.parseInt(formData.getFirst("pageSize")) : 10;
                int pageNumber = formData.getFirst("pageNumber") != null ? Integer.parseInt(formData.getFirst("pageNumber")) : 1;


                return productService.updateProductInCart(id, action).thenReturn(
                    Rendering.view("redirect:/items?search=" + search + "&sort="+sort+"&pageNumber="+pageNumber+"&pageSize="+pageSize ).build()
                );
            });
    }

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
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Rendering> uploadProducts(ServerWebExchange exchange) {
    
    return exchange.getMultipartData()
        .flatMap(multipartData -> {
        
            Map<String, Part> parts = multipartData.toSingleValueMap();
            Part part = parts.get("file");
            
            if (part instanceof FilePart filePart) {
                return productImportService.uploadProductsFromXlsx(filePart)
                    .thenReturn(Rendering.view("redirect:/items?importSuccess=true").build())
                    .onErrorResume(error -> {
                        return Mono.just(Rendering.view("redirect:/items?importSuccess=false").build());
                    });
            } else {
                return Mono.just(Rendering.view("redirect:/items?importSuccess=false").build());
            }
        });
}
}
