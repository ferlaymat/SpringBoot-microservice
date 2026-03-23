package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.example.product.type.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {


    private MockMvc mvc;

    @Mock
    private ProductService productService;


    private final BigDecimal bd1 = new BigDecimal("300.0");
    private final BigDecimal bdMin = new BigDecimal("50.0");
    private final BigDecimal bdMax = new BigDecimal("500.0");
    private final Product product = new Product(null, "nameTest", "descTest", bd1, 12, Category.ELECTRONICS, null);
    private final Product productRes = new Product(1L, "nameTest", "descTest", bd1, 12, Category.ELECTRONICS, LocalDateTime.parse("2026-03-22T00:00:00"));
    private final Product productRes2 = new Product(1L, "nameTest2", "descTest", bd1, 12, Category.ELECTRONICS, LocalDateTime.parse("2026-03-22T00:00:00"));
    private final String productString= new ObjectMapper().writeValueAsString(product);
    private final String productString2= new ObjectMapper().writeValueAsString(productRes2);

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    public void createProduct() throws Exception {
        when(productService.createProduct(product)).thenReturn(productRes);
        mvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productString)).andExpect(status()
                .isOk())
                .andExpect(jsonPath("$.id")
                        .value(1L))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProduct() throws Exception {
        when(productService.getProduct(1L)).thenReturn(productRes);
        mvc.perform(get("/api/v1/product/id/1")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id")
                        .value(1L))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProductByName() throws Exception {
        when(productService.getProductByName("nameTest")).thenReturn(productRes);
        mvc.perform(get("/api/v1/product/name/nameTest")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id")
                        .value(1L))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProductByCategory() throws Exception {
        when(productService.getProductByCategory(Category.ELECTRONICS)).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product/category/ELECTRONICS")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProductByPriceBetween() throws Exception {
        when(productService.getProductByPriceBetween(bdMin,bdMax)).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product/price?min=50.0&max=500.0")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProductByStockBetween() throws Exception {
        when(productService.getProductByStockBetween(1,20)).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product/stock?min=1&max=20")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }


    @Test
    public void getProductByCreatedAt() throws Exception {
        when(productService.getProductByCreatedAt(LocalDateTime.parse("2026-03-22T00:00:00"))).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product/date?at=2026-03-22T00:00:00")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getProductByCreatedAtBetween() throws Exception {
        when(productService.getProductByCreatedAtBetween(LocalDateTime.parse("2026-03-21T00:00:00"), LocalDateTime.parse("2026-03-23T00:00:00"))).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product/date/btw?from=2026-03-21T00:00:00&to=2026-03-23T00:00:00")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void getAllProduct() throws Exception {
        when(productService.getAllProduct()).thenReturn(List.of(productRes));
        mvc.perform(get("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void updateProduct() throws Exception {
        when(productService.updateProduct(productRes2)).thenReturn(productRes2);
        mvc.perform(put("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString2)).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id")
                        .value(1L))
                .andExpect(jsonPath("$.name")
                        .value("nameTest2"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void updateProductList() throws Exception {
        when(productService.updateProductList(List.of(productRes2))).thenReturn(List.of(productRes2));
        mvc.perform(put("/api/v1/product/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("["+productString2+"]")).andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1L))
                .andExpect(jsonPath("$[0].name")
                        .value("nameTest2"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-03-22T00:00:00"));
    }

    @Test
    public void deleteProduct() throws Exception {
        mvc.perform(delete("/api/v1/product/id/1")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status()
                        .isOk());
        verify(productService).deleteProduct(1L);
    }


    @Test
    public void deleteProductList() throws Exception {
        mvc.perform(delete("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2]")).andExpect(status()
                .isOk());
        verify(productService).deleteProductList(List.of(1L,2L));
    }
    
}
