package com.example.order.service;

import com.example.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService {

    List<OrderItem> findAllByOrderId(Long id);
}
