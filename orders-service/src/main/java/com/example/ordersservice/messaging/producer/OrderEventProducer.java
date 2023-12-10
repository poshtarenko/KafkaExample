package com.example.ordersservice.messaging.producer;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreationEvent> orderCreationKafkaTemplate;
    private final KafkaTemplate<String, ChangeOrderDestinationEvent> changeOrderDestinationKafkaTemplate;

    @Value("${topic-names.order-creation}")
    private String orderTopic;
    @Value("${topic-names.change-order-destination}")
    private String changeOrderDestinationTopic;

    @Transactional
    public void sendOrderCreationEvent(OrderCreationEvent orderEvent) {
        sendOrderCreationEvent(orderTopic, orderEvent);
    }

    @Transactional
    public void sendOrderCreationEvent(String topic, OrderCreationEvent orderEvent) {
        log.info("Sending order creation event : " + orderEvent);
        var completableFuture = orderCreationKafkaTemplate.send(topic, orderEvent);
        completableFuture.whenComplete(
                ((sendResult, throwable) -> {
                    if (throwable != null) {
                        log.warn("Error while sending event " + orderEvent);
                    } else {
                        log.info("Event sent successfully for the value {}, partition {}",
                                orderEvent, sendResult.getRecordMetadata().partition());
                    }
                })
        );
    }

    @Transactional
    public void sendChangeOrderDestinationEvent(ChangeOrderDestinationEvent event) {
        sendChangeOrderDestinationEvent(changeOrderDestinationTopic, event);
    }

    @Transactional
    public void sendChangeOrderDestinationEvent(String topic, ChangeOrderDestinationEvent event) {
        log.info("Sending change order destination event : " + event);
        var completableFuture = changeOrderDestinationKafkaTemplate.send(topic, event);
        completableFuture.whenComplete(
                ((sendResult, throwable) -> {
                    if (throwable != null) {
                        log.warn("Error while sending event " + event);
                    } else {
                        log.info("Event sent successfully for the value {}, partition {}",
                                event, sendResult.getRecordMetadata().partition());
                    }
                })
        );
    }

}
