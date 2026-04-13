package ru.yandex.practicum.mymarket.model;

public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;
    private int count;

    public ProductDto(){}

    public ProductDto(Long id, String title, String description, String imgPath, Long price, int count){
        this.id = id;
        this.title = title;
        this.count = count;
        this.description = description;
        this.price = price;
        this.imgPath = imgPath;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public Long getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public String getImgPath() {
        return imgPath;
    }
}
