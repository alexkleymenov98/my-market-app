package ru.yandex.practicum.mymarket.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "products")
public class ProductEntity {
    @Id
    private Long id;
    private String title;
    private String description;
    @Column("img_path")
    private String imgPath;
    private Long price;
    private int count;


    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getImgPath(){
        return imgPath;
    }

    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }

    public Long getPrice(){
        return price;
    }

    public void setPrice(Long price){
        this.price = price;
    }

    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    
}
