package ru.yandex.practicum.mymarket.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.yandex.practicum.mymarket.entity.ProductEntity;
import ru.yandex.practicum.mymarket.repository.ProductRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportService {
    
    private final ProductRepository productRepository;
    
    public ProductImportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public void uploadProductsFromXlsx(MultipartFile file) throws IOException {
        List<ProductEntity> products = parseExcelFile(file);
        
        productRepository.saveAll(products);
    }
    
    private List<ProductEntity> parseExcelFile(MultipartFile file) throws IOException {
        List<ProductEntity> products = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Первый лист
            
            // Пропускаем заголовок (первая строка)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                
                ProductEntity product = parseRowToProduct(row);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        
        return products;
    }
    
    private ProductEntity parseRowToProduct(Row row) {
        try {
            ProductEntity product = new ProductEntity();
            
            // Предполагаемая структура Excel:
            // Колонка 0: Название товара
            // Колонка 1: Описание товара
            // Колонка 2: url картинкитовара
            // Колонка 3: Цена
            // Колонка 4: Количество
            
            // Название (String)
            Cell titleCell = row.getCell(0);
            product.setTitle(getStringCellValue(titleCell));

            Cell descriptionCell = row.getCell(1);
            product.setDescription(getStringCellValue(descriptionCell));

            Cell imgCell = row.getCell(2);
            product.setImgPath(getStringCellValue(imgCell));
            
            // Цена (Long)
            Cell priceCell = row.getCell(3);
            product.setPrice(getLongCellValue(priceCell));
            
            // Количество (Integer)
            Cell countCell = row.getCell(4);
            product.setCount(getIntegerCellValue(countCell));
            
            return product;
            
        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }
    
    // Вспомогательные методы для чтения ячеек
    
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
    
    private Long getLongCellValue(Cell cell) {
        if (cell == null) return 0L;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0L;
                }
            default:
                return 0L;
        }
    }
    
    private Integer getIntegerCellValue(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
}
