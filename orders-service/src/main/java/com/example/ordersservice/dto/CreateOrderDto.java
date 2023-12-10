package com.example.ordersservice.dto;

import java.math.BigDecimal;

public record CreateOrderDto(
        String product,
        String category,
        BigDecimal price,
        String customerName,
        String destination
) {
}
