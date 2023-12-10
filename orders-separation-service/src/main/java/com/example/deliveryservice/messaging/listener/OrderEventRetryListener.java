package com.example.deliveryservice.messaging.listener;

import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventRetryListener {

    private final DeliveryService deliveryService;

//    @KafkaListener(topics = "${topics.order.name-retry}",
//            autoStartup = "${retryListener.startup:true}",
//            groupId = "retry-listener-group")
//    void onOrderEventRetry(OrderEvent orderEvent) {
//        log.info("Got retry order event : " + orderEvent);
//        deliveryService.handleOrderEvent(orderEvent);
//    }

}
