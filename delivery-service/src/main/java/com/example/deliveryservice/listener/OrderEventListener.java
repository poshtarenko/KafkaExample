package com.example.deliveryservice.listener;

import com.example.deliveryservice.dto.Order;
import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "order", groupId = "ORDERS_SERVICE_GROUP")
    void listener(Order order) {
        log.info("Got order : " + order);
        deliveryService.deliver(order);
    }

}
