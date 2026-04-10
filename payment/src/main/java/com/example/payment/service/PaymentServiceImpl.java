package com.example.payment.service;

import com.example.common.event.object.OrderCreatedEvent;
import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(OrderCreatedEvent event) {
        Payment newPayment = new Payment(event.getOrderId(), event.getTotalAmount());
        return this.paymentRepository.save(newPayment);
    }
}
