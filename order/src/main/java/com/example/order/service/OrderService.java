package com.example.order.service;

import com.example.order.dto.CustomerOrder;
import com.example.order.entity.Order;
import com.example.order.type.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Order createOrder(CustomerOrder customerOrder);
    Order getOrderById(Long id);
    Page<Order> getOrderByCustomer(int page, int size, String sorBy, String sortOrder, String email);
    Page<Order> getAllOrders(int page, int size, String sorBy, String sortOrder);
    Order updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id);

 }
