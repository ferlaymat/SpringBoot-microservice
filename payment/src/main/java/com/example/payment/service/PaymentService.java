package com.example.payment.service;

import com.example.common.event.object.OrderCreatedEvent;
import com.example.payment.entity.Payment;

public interface PaymentService {
    Payment processPayment(OrderCreatedEvent event);
}
