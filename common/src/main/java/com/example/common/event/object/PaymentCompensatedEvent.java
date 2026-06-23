package com.example.common.event.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentCompensatedEvent {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;

}