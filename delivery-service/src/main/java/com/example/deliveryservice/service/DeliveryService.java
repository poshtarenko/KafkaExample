package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.Order;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    public void deliver(Order order){
        System.out.printf("Delivering order %s to destination %s%n", order, order.getDeliveryDestination());
    }

}
