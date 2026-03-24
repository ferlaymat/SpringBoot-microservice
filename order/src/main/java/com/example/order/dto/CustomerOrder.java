package com.example.order.dto;

import com.example.order.entity.OrderItem;

import java.util.Set;

public record CustomerOrder(String email, Set<OrderItem> orderItemSet) {
}
