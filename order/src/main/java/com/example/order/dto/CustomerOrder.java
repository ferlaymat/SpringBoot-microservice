package com.example.order.dto;

import java.util.Map;

public record CustomerOrder(String email, Map<Long, Integer> orderItemMap) {
}
