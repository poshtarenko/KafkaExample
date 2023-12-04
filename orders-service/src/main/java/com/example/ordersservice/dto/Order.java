package com.example.ordersservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Order {
    Long id;
    String customerName;
    String deliveryDestination;
}
