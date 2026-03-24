package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItemSet WHERE o.id = :id")
    Order findOrderByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItemSet WHERE o.customerEmail = :email")
    Page<Order> findAllOrdersByEmailWithItems(@Param("email") String email, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItemSet")
    Page<Order> findAllOrdersWithItems(Pageable pageable);
}
