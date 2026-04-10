package com.example.payment.event.consumer;

import com.example.common.event.object.OrderCreatedEvent;
import com.example.common.event.object.PaymentCompletedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.payment.entity.Payment;
import com.example.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Value("${kafka.topics.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    public PaymentEventConsumer(PaymentService paymentService,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "payment-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        try {
            Payment payment = paymentService.processPayment(event);

            PaymentCompletedEvent completed = new PaymentCompletedEvent();
            completed.setOrderId(event.getOrderId());
            completed.setPaymentId(payment.getId());

            kafkaTemplate.send(paymentCompletedTopic,
                event.getOrderId().toString(), completed);

        } catch (Exception ex) {
            PaymentFailedEvent failed = new PaymentFailedEvent();
            failed.setOrderId(event.getOrderId());
            failed.setReason(ex.getMessage());

            kafkaTemplate.send(paymentFailedTopic,
                event.getOrderId().toString(), failed);
        }
    }
}