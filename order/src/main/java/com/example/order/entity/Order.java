package com.example.order.entity;

import com.example.order.type.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)// needed to use @CreatedDate
@Entity
@Table(name = "orders")//ORDER is a RESERVED KEYWORDS for h2 db
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private Set<OrderItem> orderItemSet;

    @NotBlank(message = "Customer email is mandatory")
    @Size(max = 100, message = "Customer email size must not exceed 100 car.")
    @Column(nullable = false, unique = true, length = 100)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status;

    @NotNull(message = "total amount is mandatory")
    @Min(value = 0, message = "total amount could not be negative")
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedDate
    private LocalDateTime updatedAt;
}
