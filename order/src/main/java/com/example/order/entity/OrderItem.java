package com.example.order.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@DynamicUpdate
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @NotNull(message = "product id is mandatory")
    private Long productId;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name size must not exceed 30 car.")
    @Column(nullable = false, length = 30)
    private String productName;

    @NotNull(message = "unity price is mandatory")
    @Min(value = 0, message = "unity price could not be negative")
    @Column(nullable = false)
    private BigDecimal unityPrice;

    @NotNull(message = "quantity mandatory")
    @Min(value = 0, message = "quantity could not be negative")
    @Column(nullable = false)
    private Integer quantity;
}
