package com.example.ordersservice.messaging.listener;

import com.example.common.messaging.models.UpdateOrderStatusEvent;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = "${topic-names.update-order-status}", groupId = "${spring.kafka.consumer.group-id}")
    void onUpdateOrderStatusEvent(UpdateOrderStatusEvent event, Acknowledgment acknowledgment) {
        log.info("Got update order status event : " + event);
        orderService.updateStatus(event);
        acknowledgment.acknowledge();
    }

}
