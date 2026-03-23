package com.example.product.entity;


import com.example.product.type.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@DynamicUpdate
@Entity
@EntityListeners(AuditingEntityListener.class)// needed to use @CreatedDate
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name size must not exceed 30 car.")
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Size(max = 500, message = "desc size must not exceed 500 car.")
    @Column(length = 500)
    private String description;


    @NotNull(message = "price is mandatory")
    @Min(value = 0, message = "price could not be negative")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "stock is mandatory")
    @Min(value = 0, message = "stock could not be negative")
    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Category category;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
