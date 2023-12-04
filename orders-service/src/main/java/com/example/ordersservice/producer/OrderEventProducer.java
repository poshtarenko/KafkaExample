package com.example.ordersservice.producer;

import com.example.ordersservice.dto.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public void sendOrderCreationEvent(String topic, Order order) {
        kafkaTemplate.send(topic, order);
    }

}
