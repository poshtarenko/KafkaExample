package com.example.deliveryservice.service;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
import com.example.common.messaging.models.OrderStatus;
import com.example.common.messaging.models.UpdateOrderStatusEvent;
import com.example.deliveryservice.domain.Delivery;
import com.example.deliveryservice.messaging.producer.UpdateOrderStatusEventProducer;
import com.example.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.deliveryservice.domain.DeliveryStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UpdateOrderStatusEventProducer updateOrderStatusEventProducer;

    private static final int DELIVERY_PREPARATION_DURATION_MS = 7000;
    private static final int DELIVERY_SHIPPING_DURATION_MS = 10000;

    @Transactional
    public void startDelivery(OrderCreationEvent event) {
        validateDestination(event.getDeliveryDestination().toString());
        Delivery delivery = Delivery.builder()
                .orderId(event.getId())
                .product(event.getProduct().toString())
                .customerName(event.getCustomerName().toString())
                .destination(event.getDeliveryDestination().toString())
                .status(PREPARATION)
                .createdAt(LocalDateTime.now())
                .build();
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void changeDeliveryDestination(ChangeOrderDestinationEvent event) {
        validateDestination(event.getDeliveryDestination().toString());
        Delivery delivery = deliveryRepository.findByOrderId(event.getId());
        if (ON_THE_WAY.equals(delivery.getStatus())) {
            throw new RuntimeException("Delivery already in process, can't change destination");
        }
        if (DELIVERED.equals(delivery.getStatus())) {
            throw new RuntimeException("Delivery already completed");
        }
        if (event.getDeliveryDestination().toString().equals(delivery.getDestination())) {
            throw new RuntimeException("Trying change destination to same one");
        }
        delivery.setDestination(event.getDeliveryDestination().toString());
    }

    @Transactional
    @Scheduled(fixedRate = DELIVERY_PREPARATION_DURATION_MS)
    public void startShipping() {
        List<Delivery> deliveries = deliveryRepository.findByStatus(PREPARATION);
        deliveries.forEach(delivery -> {
            delivery.setStatus(ON_THE_WAY);
            deliveryRepository.save(delivery);
            UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(delivery.getOrderId(), OrderStatus.ON_THE_WAY);
            updateOrderStatusEventProducer.sendEvent(event);
        });
    }

    @Transactional
    @Scheduled(fixedRate = DELIVERY_SHIPPING_DURATION_MS)
    public void completeShipping() {
        List<Delivery> deliveries = deliveryRepository.findByStatus(ON_THE_WAY);
        deliveries.forEach(delivery -> {
            delivery.setStatus(DELIVERED);
            UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(delivery.getOrderId(), OrderStatus.DELIVERED);
            updateOrderStatusEventProducer.sendEvent(event);
        });
    }

    private void validateDestination(String destination) {
        if (destination.isBlank()) {
            log.warn("Wrong delivery destination");
            throw new IllegalArgumentException("Wrong delivery destination");
        }
    }

}
