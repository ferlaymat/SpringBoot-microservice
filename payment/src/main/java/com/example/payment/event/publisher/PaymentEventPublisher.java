package com.example.payment.event.publisher;

import com.example.common.event.object.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;


    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentCompleted(long orderId, long paymentId) {
        PaymentCompletedEvent completed = new PaymentCompletedEvent();
        completed.setOrderId(orderId);
        completed.setPaymentId(paymentId);

        kafkaTemplate.send(paymentCompletedTopic,
                String.valueOf(orderId), completed);
    }

    public void publishPaymentFailed(long orderId, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(orderId, reason);
        kafkaTemplate.send(paymentFailedTopic,
                String.valueOf(orderId), event);
    }

}