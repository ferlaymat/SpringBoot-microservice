package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import com.example.product.type.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
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

    @Override
    public List<Product> reserveStock(Map<Long, Integer> reservationMap) {
        List<Long> idList = reservationMap.keySet().stream().toList();
        List<Product> productList = getProductList(idList);

        for (Map.Entry<Long, Integer>item : reservationMap.entrySet()) {
            Product product = productList.stream().filter(p -> p.getId() == item.getKey()).findFirst().get();
            if (item.getValue() > product.getStock()) {
                log.info("Not enough quantity available");
                throw new IllegalStateException(
                        String.format("Error: Not enough quantity available: {id:%s, available:%s, required:%s}", product.getId(), product.getStock(), item.getKey(), item.getValue()));
            }
            //update new item's quantity
            product.setStock(product.getStock() - item.getValue());
        }
        return updateProductList(productList);
    }
}
