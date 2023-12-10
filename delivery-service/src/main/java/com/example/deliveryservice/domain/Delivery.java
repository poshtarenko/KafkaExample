package com.example.deliveryservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "product")
    String product;

    @Column(name = "customer_name")
    String customerName;

    @Column(name = "destination")
    String destination;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    DeliveryStatus status;

    @Column(name = "delivered_at")
    LocalDateTime deliveredAt;

    @Column(name = "created_at")
    LocalDateTime createdAt;

}
