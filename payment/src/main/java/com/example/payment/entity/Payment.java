package com.example.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "orderId is mandatory")
    @Column(nullable = false)
    private Long orderId;


    @NotNull(message = "amount is mandatory")
    @Min(value = 0, message = "amount could not be negative")
    @Column(nullable = false)
    private BigDecimal amount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Payment(Long orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
