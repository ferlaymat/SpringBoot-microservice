package com.example.order.service;

import com.example.common.event.object.PaymentCompletedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.common.event.object.StockCompensatedEvent;
import com.example.order.dto.CustomerOrder;
import com.example.order.entity.Order;
import com.example.order.type.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(CustomerOrder customerOrder);
    Map<Long, Integer> cancelOrder(Long id);
    void onPaymentCompleted(PaymentCompletedEvent event);
    void onPaymentFailed(PaymentFailedEvent event);
    void onStockCompensated(StockCompensatedEvent event);

 }
