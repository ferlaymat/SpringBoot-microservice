package com.example.order.service;

import com.example.common.event.object.PaymentCompletedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.order.dto.CustomerOrder;
import com.example.order.entity.Order;
import com.example.order.type.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(CustomerOrder customerOrder);
    Order getOrderById(Long id);
    Page<Order> getOrderByCustomer(int page, int size, String sorBy, String sortOrder, String email);
    Page<Order> getAllOrders(int page, int size, String sorBy, String sortOrder);
    Order updateOrderStatus(Long id, OrderStatus status);
    Map<Long, Integer> cancelOrder(Long id);
    void onPaymentCompleted(PaymentCompletedEvent event);
    void onPaymentFailed(PaymentFailedEvent event);

 }
