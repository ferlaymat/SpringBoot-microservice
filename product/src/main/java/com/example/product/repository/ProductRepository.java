package com.example.product.repository;

import com.example.product.entity.Product;
import com.example.product.type.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getProductByName(String name);
    List<Product> getProductByCategory(Category ctg);
    List<Product> getProductByCreatedAt(LocalDateTime createDate);
    List<Product> getProductByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getProductByStockBetween(Integer minQty, Integer maxQty);
    List<Product> getProductByCreatedAtBetween(LocalDateTime minDate, LocalDateTime maxDate);
}
