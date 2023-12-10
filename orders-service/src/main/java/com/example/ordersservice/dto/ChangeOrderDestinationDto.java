package com.example.ordersservice.dto;

public record ChangeOrderDestinationDto(
        Long orderId,
        String destination
) {
}
