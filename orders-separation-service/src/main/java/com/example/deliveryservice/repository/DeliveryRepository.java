package com.example.deliveryservice.repository;

import com.example.deliveryservice.domain.Delivery;
import com.example.deliveryservice.domain.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByStatus(DeliveryStatus status);

    Delivery findByOrderId(Long orderId);

}
