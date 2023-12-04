package com.example.ordersservice.controller;

import com.example.ordersservice.dto.Order;
import com.example.ordersservice.producer.OrderEventProducer;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderEventProducer orderEventProducer;

    @PostMapping("/orders/create")
    private Order create() {
        Order order = orderService.create();
        log.info("Sending order : " + order);
        orderEventProducer.sendOrderCreationEvent("order", order);
        return order;
    }

}
