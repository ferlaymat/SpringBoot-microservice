package com.example.common.event.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockReservedEvent {
    private Long orderId;
    private BigDecimal totalAmount;
    private Map<Long, Integer> orderMap;

}