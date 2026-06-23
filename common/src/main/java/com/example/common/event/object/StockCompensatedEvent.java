package com.example.common.event.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockCompensatedEvent {
    private Long orderId;
    private Map<Long, Integer> items;

}