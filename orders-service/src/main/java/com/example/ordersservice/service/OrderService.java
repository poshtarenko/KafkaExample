package com.example.ordersservice.service;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
import com.example.common.messaging.models.UpdateOrderStatusEvent;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.ChangeOrderDestinationDto;
import com.example.ordersservice.dto.CreateOrderDto;
import com.example.ordersservice.messaging.producer.OrderEventProducer;
import com.example.ordersservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ordersservice.domain.OrderStatus.CREATED;
import static com.example.ordersservice.domain.OrderStatus.DELIVERED;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Order create(CreateOrderDto request) {
        Order order = Order.builder()
                .product(request.product())
                .category(request.category())
                .customerName(request.customerName())
                .price(request.price())
                .deliveryDestination(request.destination())
                .status(CREATED)
                .isCompleted(false)
                .build();
        Order savedOrder = orderRepository.save(order);
        OrderCreationEvent event = buildOrderCreationEvent(savedOrder);
        orderEventProducer.sendOrderCreationEvent(event);
        return savedOrder;
    }

    @Transactional
    public Order changeDestination(ChangeOrderDestinationDto request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDeliveryDestination().equals(request.destination())) {
            throw new RuntimeException("Same destination");
        }
        order.setDeliveryDestination(request.destination());
        Order updatedOrder = orderRepository.save(order);
        ChangeOrderDestinationEvent event = buildChangeOrderDestinationEvent(updatedOrder);
        orderEventProducer.sendChangeOrderDestinationEvent(event);
        return updatedOrder;
    }

    @Transactional
    public void updateStatus(UpdateOrderStatusEvent event) {
        Order order = orderRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus status = switch (event.getStatus()) {
            case ON_THE_WAY -> OrderStatus.ON_THE_WAY;
            case DELIVERED -> OrderStatus.DELIVERED;
            default -> throw new RuntimeException("Trying to pass unexpected order status");
        };
        order.setStatus(status);
        if (DELIVERED.equals(status)) {
            order.setCompletedAt(LocalDateTime.now());
        }
        orderRepository.save(order);
    }

    private OrderCreationEvent buildOrderCreationEvent(Order order) {
        return OrderCreationEvent.newBuilder()
                .setId(order.getId())
                .setProduct(order.getProduct())
                .setCategory(order.getCategory())
                .setPrice(order.getPrice().toString())
                .setCustomerName(order.getCustomerName())
                .setDeliveryDestination(order.getDeliveryDestination())
                .setStatus(com.example.common.messaging.models.OrderStatus.valueOf(order.getStatus().name()))
                .setIsCompleted(order.getIsCompleted())
                .setCompletedAt(order.getCompletedAt())
                .build();
    }

    private ChangeOrderDestinationEvent buildChangeOrderDestinationEvent(Order order) {
        return ChangeOrderDestinationEvent.newBuilder()
                .setId(order.getId())
                .setDeliveryDestination(order.getDeliveryDestination())
                .build();
    }
}
