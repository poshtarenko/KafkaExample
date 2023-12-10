package com.example.ordersservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product")
    String product;

    @Column(name = "category")
    String category;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "customer_name")
    String customerName;

    @Column(name = "delivery_destination")
    String deliveryDestination;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(name = "is_completed")
    Boolean isCompleted;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

}
