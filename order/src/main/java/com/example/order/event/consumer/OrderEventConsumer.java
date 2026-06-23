package com.example.order.event.consumer;

import com.example.common.event.object.PaymentCompletedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.common.event.object.StockCompensatedEvent;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    private final OrderService orderService;
    private final OrderEventConsumer orderEventConsumer;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public OrderEventConsumer(OrderService orderService,
                              OrderEventConsumer orderEventConsumer,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderService = orderService;
        this.orderEventConsumer = orderEventConsumer;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}", groupId = "order-group")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        try {
        orderService.onPaymentCompleted(event);

        } catch (Exception ex) {
            log.error("CRITICAL: completion order {} failed after Resilience4j retries",
                    event.getOrderId(), ex);
            // TODO: Notification admin / Dead Letter Queue
        }
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "order-group")
    public void onPaymentFailed(PaymentFailedEvent event) {

            orderService.onPaymentFailed(event);
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}", groupId = "order-group")
    public void onStockCompensated(StockCompensatedEvent event) {
        try {
            orderService.onStockCompensated(event);

        } catch (Exception ex) {
            log.error("CRITICAL: compensated order {} failed after Resilience4j retries",
                    event.getOrderId(), ex);
            // TODO: Notification admin / Dead Letter Queue
        }
    }
}