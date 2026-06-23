package com.example.order.event.publisher;

import com.example.common.event.object.OrderCancelledEvent;
import com.example.common.event.object.OrderCreatedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setTotalAmount(order.getTotalAmount());
        event.setOrderMap(order.getOrderItemSet().stream().collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity)));
        kafkaTemplate.send(orderCreatedTopic,
                order.getId().toString(), event);
    }

    public void publishOrderCancelled(Long orderId, String reason , Map<Long, Integer> cancelMap){
        OrderCancelledEvent event = new OrderCancelledEvent();
        event.setOrderId(orderId);
        event.setReason(reason);
        event.setOrderMap(cancelMap);
        kafkaTemplate.send(orderCreatedTopic,
                String.valueOf(orderId), event);
    }
}