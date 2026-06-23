package com.example.product.event.consumer;

import com.example.common.event.object.OrderCancelledEvent;
import com.example.common.event.object.OrderCreatedEvent;
import com.example.product.event.publisher.ProductEventPublisher;
import com.example.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductEventConsumer {

    private final ProductService productService;
    private final ProductEventPublisher productEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public ProductEventConsumer(ProductService productService,
                                ProductEventPublisher productEventPublisher,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.productService = productService;
        this.productEventPublisher = productEventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "product-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Processing stock reservation for orderId: {}", event.getOrderId());

        try {
            productService.reserveStock(event.getOrderMap());
            productEventPublisher.publishStockReserved(event.getOrderId(), event.getTotalAmount(), event.getOrderMap());
            log.info("Stock reserved successfully for orderId: {}", event.getOrderId());
        } catch (Exception ex) {
            log.error("Stock reservation failed for orderId: {}", event.getOrderId(), ex);
            productEventPublisher.publishStockFailed(event.getOrderId(), ex.getMessage());
        }

    }

    @KafkaListener(topics = "${kafka.topics.order-cancelled}", groupId = "product-group")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Processing stock compensation for orderId: {}", event.getOrderId());

        try {
            productService.cancelStock(event.getOrderMap());
            productEventPublisher.publishStockCompensated(event.getOrderId(), event.getOrderMap());
            log.info("Stock released successfully for orderId: {}", event.getOrderId());
        } catch (Exception ex) {
            log.error("CRITICAL: Stock compensation failed for orderId: {}", event.getOrderId(), ex);
            // TODO: Notification admin / Dead Letter Queue
        }

    }
}