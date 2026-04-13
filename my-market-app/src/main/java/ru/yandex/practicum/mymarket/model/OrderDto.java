package ru.yandex.practicum.mymarket.model;

import java.util.List;

public class OrderDto {
    private Long id;
    private Long totalSum;
    private List<ProductDto> items;

    public OrderDto(Long id, List<ProductDto> items){
        this.id = id;
        this.items = items;

        this.totalSum = items.stream()
        .map(ProductDto::getPrice)
        .reduce(0L, Long::sum);
    }

    public Long getId() {
        return id;
    }

    public List<ProductDto> getItems() {
        return items;
    }

    public Long getTotalSum() {
        return totalSum;
    }
}
