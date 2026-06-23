package com.example.payment.event.consumer;

import com.example.common.event.object.StockReservedEvent;
import com.example.payment.entity.Payment;
import com.example.payment.event.publisher.PaymentEventPublisher;
import com.example.payment.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final PaymentEventPublisher paymentEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public PaymentEventConsumer(PaymentService paymentService,
                                PaymentEventPublisher paymentEventPublisher,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentService = paymentService;
        this.paymentEventPublisher = paymentEventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topics.stock-reserved}", groupId = "payment-group")
    public void onStockReserved(StockReservedEvent event) {
        try {
            Payment payment = paymentService.processPayment(event);
            paymentEventPublisher.publishPaymentCompleted(event.getOrderId(), payment.getId());

        } catch (Exception ex) {

            paymentEventPublisher.publishPaymentFailed(event.getOrderId(), ex.getMessage());
        }
    }
}