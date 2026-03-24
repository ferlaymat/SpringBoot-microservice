package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import com.example.product.type.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Product> getProductList(List<Long> idList) {
        return productRepository.findAllById(idList);
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.getProductByName(name);
    }

    @Override
    public List<Product> getProductByCategory(Category ctg) {
        return productRepository.getProductByCategory(ctg);
    }

    @Override
    public List<Product> getProductByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.getProductByPriceBetween(minPrice,maxPrice);
    }

    @Override
    public List<Product> getProductByStockBetween(Integer minQty, Integer maxQty) {
        return productRepository.getProductByStockBetween(minQty,maxQty);
    }

    @Override
    public List<Product> getProductByCreatedAt(LocalDateTime createDate) {
        return productRepository.getProductByCreatedAt(createDate);
    }

    @Override
    public List<Product> getProductByCreatedAtBetween(LocalDateTime minDate, LocalDateTime maxDate) {
        return productRepository.getProductByCreatedAtBetween(minDate,maxDate);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> updateProductList(List<Product> productList) {
        return productRepository.saveAll(productList);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProductList(List<Long> idList) {
        productRepository.deleteAllById(idList);
    }
}
