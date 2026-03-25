package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.type.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProductService {

    Product createProduct(Product product);

    Product getProduct(Long id);

    List<Product> getProductList(List<Long> idList);

    Product getProductByName(String name);

    List<Product> getProductByCategory(Category ctg);

    List<Product> getProductByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> getProductByStockBetween(Integer minQty, Integer maxQty);

    List<Product> getProductByCreatedAt(LocalDateTime createDate);

    List<Product> getProductByCreatedAtBetween(LocalDateTime minDate, LocalDateTime maxDate);

    List<Product> getAllProduct();

    Product updateProduct(Product product);

    List<Product> updateProductList(List<Product> productList);

    void deleteProduct(Long id);

    void deleteProductList(List<Long> idList);

    List<Product> reserveStock(Map<Long, Integer> reservationMap);
}
