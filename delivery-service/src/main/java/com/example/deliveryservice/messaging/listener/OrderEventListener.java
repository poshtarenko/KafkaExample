package com.example.deliveryservice.messaging.listener;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "${topic-names.cheap-order-creation}", groupId = "${spring.kafka.consumer.group-id}")
    void onCheapOrderCreationEvent(OrderCreationEvent event, Acknowledgment acknowledgment) {
        log.info("Got cheap order creation event : " + event);
        deliveryService.startDelivery(event);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${topic-names.expensive-order-creation}", groupId = "${spring.kafka.consumer.group-id}")
    void onExpensiveOrderCreationEvent(OrderCreationEvent event, Acknowledgment acknowledgment) {
        log.info("Got expensive order creation event : " + event);
        deliveryService.startDelivery(event);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${topic-names.change-order-destination}", groupId = "${spring.kafka.consumer.group-id}")
    void onChangeOrderDestinationEvent(ChangeOrderDestinationEvent event, Acknowledgment acknowledgment) {
        log.info("Got change order destination event : " + event);
        deliveryService.changeDeliveryDestination(event);
        acknowledgment.acknowledge();
    }

}
