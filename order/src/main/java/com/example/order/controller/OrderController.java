package com.example.order.controller;


import com.example.order.dto.CustomerOrder;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CustomerOrder customerOrder){
        return ResponseEntity.ok(this.orderService.createOrder(customerOrder));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") Long id){
        this.orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
