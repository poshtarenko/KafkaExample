package com.example.reportsservice.messaging.listener;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.reportsservice.service.ProductSalesReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final ProductSalesReportService productSalesReportService;

    @KafkaListener(topics = "${topic-names.order-creation}", groupId = "${spring.kafka.consumer.group-id}")
    void onOrderCreatedEvent(OrderCreationEvent event, Acknowledgment acknowledgment) {
        log.info("Got update order status event : " + event);
        productSalesReportService.updateReport(event);
        acknowledgment.acknowledge();
    }

}
