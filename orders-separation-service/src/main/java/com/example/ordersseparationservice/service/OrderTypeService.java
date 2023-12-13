package com.example.ordersseparationservice.service;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.ordersseparationservice.domain.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderTypeService {

    private final BigDecimal MIN_EXPENSIVE_ORDER_PRICE = new BigDecimal("300.00");

    public OrderType defineOrderType(OrderCreationEvent orderCreationEvent){
        BigDecimal price = new BigDecimal(orderCreationEvent.getPrice().toString());
        if (price.compareTo(MIN_EXPENSIVE_ORDER_PRICE) >= 0) {
            return OrderType.EXPENSIVE;
        } else {
            return OrderType.CHEAP;
        }
    }

}
