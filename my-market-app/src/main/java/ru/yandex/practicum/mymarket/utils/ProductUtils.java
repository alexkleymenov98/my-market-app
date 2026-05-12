package ru.yandex.practicum.mymarket.utils;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.practicum.mymarket.entity.ProductEntity;

public class ProductUtils {
    public static Long getProductsTotal(List<ProductEntity> products){
        return products.stream()
         .map(ProductEntity::getPrice)
         .reduce(0L, Long::sum);
    }

    public static List<List<ProductEntity>> mapToRowProducts(List<ProductEntity> products, int countOneRow){
        List<List<ProductEntity>> result = new ArrayList<>();
        List<ProductEntity> row = new ArrayList<>();
    
        for (int i = 0; i < products.size(); i++) {
            row.add(products.get(i));
        
            if ((i + 1) % countOneRow == 0 || i == products.size() - 1) {
                result.add(new ArrayList<>(row));
                row.clear();
            }
        }
    
        return result;
    }
}
