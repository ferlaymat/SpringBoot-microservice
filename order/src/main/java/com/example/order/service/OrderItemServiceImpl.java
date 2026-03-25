package com.example.order.service;


import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService{

    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> findAllByOrderId(Long id) {
        return this.orderItemRepository.findByOrderId(id);
    }
}
