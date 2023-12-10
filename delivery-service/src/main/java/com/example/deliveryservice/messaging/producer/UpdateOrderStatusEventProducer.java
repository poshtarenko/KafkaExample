package com.example.deliveryservice.messaging.producer;

import com.example.common.messaging.models.UpdateOrderStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderStatusEventProducer {

    private final KafkaTemplate<String, UpdateOrderStatusEvent> kafkaTemplate;

    @Value("${topic-names.update-order-status}")
    private String updateOrderStatusTopic;

    @Transactional
    public void sendEvent(UpdateOrderStatusEvent event) {
        sendEvent(updateOrderStatusTopic, event);
    }

    @Transactional
    public void sendEvent(String topic, UpdateOrderStatusEvent event) {
        log.info("Sending update order status event : " + event);
        var completableFuture = kafkaTemplate.send(topic, event);
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
