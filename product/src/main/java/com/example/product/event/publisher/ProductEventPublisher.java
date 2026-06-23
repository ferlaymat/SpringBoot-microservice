package com.example.product.event.publisher;

import com.example.common.event.object.StockCompensatedEvent;
import com.example.common.event.object.StockFailedEvent;
import com.example.common.event.object.StockReservedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.stock-reserved}")
    private String stockReservedTopic;

    @Value("${kafka.topics.stock-failed}")
    private String stockFailedTopic;

    @Value("${kafka.topics.stock-compensated}")
    private String stockCompensatedTopic;


    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStockReserved(long orderId, BigDecimal totalAmount, Map<Long, Integer> items) {
        StockReservedEvent event = new StockReservedEvent(orderId, totalAmount, items);
        kafkaTemplate.send(stockReservedTopic,
                String.valueOf(orderId), event);
    }

    public void publishStockFailed(long orderId, String reason) {
        StockFailedEvent event = new StockFailedEvent(orderId, reason);
        kafkaTemplate.send(stockFailedTopic,
                String.valueOf(orderId), event);
    }

    public void publishStockCompensated(long orderId, Map<Long, Integer> items) {
        StockCompensatedEvent event = new StockCompensatedEvent(orderId, items);
        kafkaTemplate.send(stockCompensatedTopic,
                String.valueOf(orderId), event);
    }
}