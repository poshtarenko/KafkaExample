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

    @KafkaListener(topics = "${topic-names.cheap-order-creation}", groupId = "${topic-consumer-groups.cheap-order-creation}")
    void onCheapOrderCreationEvent(OrderCreationEvent event, Acknowledgment acknowledgment) {
        log.info("Got cheap order creation event : " + event);
        deliveryService.startDelivery(event);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${topic-names.expensive-order-creation}", groupId = "${topic-consumer-groups.expensive-order-creation}")
    void onExpensiveOrderCreationEvent(OrderCreationEvent event, Acknowledgment acknowledgment) {
        log.info("Got expensive order creation event : " + event);
        deliveryService.startDelivery(event);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${topic-names.change-order-destination}", groupId = "${topic-consumer-groups.change-order-destination}")
    void onChangeOrderDestinationEvent(ChangeOrderDestinationEvent event, Acknowledgment acknowledgment) {
        log.info("Got change order destination event : " + event);
        deliveryService.changeDeliveryDestination(event);
        acknowledgment.acknowledge();
    }

}
