package com.example.product.controller;


import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.example.product.type.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product API manager", description = "APIs which allow to manage products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(description = "Insert new product in database")
    public ResponseEntity<Product> createProduct(@Parameter(description = "Required product to be saved") @RequestBody Product product){
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/id/{id}")
    @Operation(description = "Fetch a product from database by its id")
    public ResponseEntity<Product> getProduct(@Parameter(description = "Id of a specific product") @PathVariable("id") Long id){
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping("/list")
    @Operation(description = "Fetch list of products from database by their ids")
    public ResponseEntity<List<Product>> getProductList(@Parameter(description = "Id of a specific product") @RequestBody List<Long> idList){
        return ResponseEntity.ok(productService.getProductList(idList));
    }

    @GetMapping("/name/{name}")
    @Operation(description = "Fetch a product from database by its name")
    public ResponseEntity<Product> getProductByName(@Parameter(description = "Name of a specific product") @PathVariable("name") String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @GetMapping("/category/{ctg}")
    @Operation(description = "Fetch list of products from database by category")
    public ResponseEntity<List<Product>> getProductByCategory(@Parameter(description = "Product category") @PathVariable("ctg") Category ctg){
        return ResponseEntity.ok(productService.getProductByCategory(ctg));
    }

    @GetMapping("/price")
    @Operation(description = "Fetch list of products from database included in price range")
    public ResponseEntity<List<Product>> getProductByPriceBetween(@Parameter(description = "Minimum included price") @RequestParam("min")BigDecimal minPrice, @Parameter(description = "Maximum included price") @RequestParam("max")BigDecimal maxPrice){
        return ResponseEntity.ok(productService.getProductByPriceBetween(minPrice, maxPrice));
    }

    @GetMapping("/stock")
    @Operation(description = "Fetch list of products from database included in quantity range")
    public ResponseEntity<List<Product>> getProductByStockBetween(@Parameter(description = "Minimum included quantity") @RequestParam("min")Integer minQty, @Parameter(description = "Maximum included quantity") @RequestParam(value = "max", required = false, defaultValue = Integer.MAX_VALUE+ "") Integer maxQty){
        return ResponseEntity.ok(productService.getProductByStockBetween(minQty, maxQty));
    }

    @GetMapping("/date")
    @Operation(description = "Fetch list of products from database for a specific datetime")
    public ResponseEntity<List<Product>> getProductByCreatedAt(@Parameter(description = "Creation date of products") @RequestParam("at") LocalDateTime createDate){
        return ResponseEntity.ok(productService.getProductByCreatedAt(createDate));
    }

    @GetMapping("/date/btw")
    @Operation(description = "Fetch list of products from database included in date range")
    public ResponseEntity<List<Product>> getProductByCreatedAtBetween(@Parameter(description = "From included creation date of products") @RequestParam("from")LocalDateTime minDate, @Parameter(description = "To included creation date of products") @RequestParam("to")LocalDateTime maxDate){
        return ResponseEntity.ok(productService.getProductByCreatedAtBetween(minDate,maxDate));
    }

    @GetMapping
    @Operation(description = "Fetch all products from database")
    public ResponseEntity<List<Product>> getAllProduct(){
        return ResponseEntity.ok(productService.getAllProduct());
    }

    @PutMapping
    @Operation(description = "Update a specific product in database")
    public ResponseEntity<Product> updateProduct(@Parameter(description = "Updated product") @RequestBody Product product){
        return ResponseEntity.ok(productService.updateProduct(product));
    }

    @PutMapping("/list")
    @Operation(description = "Update list of products in database")
    public ResponseEntity<List<Product>> updateProductList(@Parameter(description = "Updated product list") @RequestBody List<Product> productList){
        return ResponseEntity.ok(productService.updateProductList(productList));
    }

    @DeleteMapping("/id/{id}")
    @Operation(description = "Delete a product to database by its id")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "Id of a specific product") @PathVariable("id") Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(description = "Delete list of products to database by their ids")
    public ResponseEntity<Void> deleteProductList(@Parameter(description = "List of product ids") @RequestBody List<Long> idList){
        productService.deleteProductList(idList);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reserve")
    @Operation(description = "Reserve specific quantity of products")
    public ResponseEntity<List<Product>> reserveStock(@Parameter(description = "Map of quantity by product id") @RequestBody Map<Long,Integer> reservationMap){
        return ResponseEntity.ok(productService.reserveStock(reservationMap));
    }
}
