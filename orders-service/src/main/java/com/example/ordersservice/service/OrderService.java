package com.example.ordersservice.service;

import com.example.ordersservice.dto.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static Long id = 0L;

    public Order create() {
        id++;
        return Order.builder()
                .id(id)
                .customerName("Customer №" + id)
                .deliveryDestination("Destination №" + id)
                .build();
    }

}
