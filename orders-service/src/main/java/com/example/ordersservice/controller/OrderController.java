package com.example.ordersservice.controller;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.dto.ChangeOrderDestinationDto;
import com.example.ordersservice.dto.CreateOrderDto;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private static final String ORDER_GET_URL = "/get";
    private static final String ORDER_CREATE_URL = "/create";
    private static final String ORDER_CHANGE_DESTINATION_URL = "/change-destination";

    private final OrderService orderService;

    @GetMapping(ORDER_GET_URL)
    private Order get(@RequestBody Long id) {
        return orderService.get(id);
    }

    @PostMapping(ORDER_CREATE_URL)
    private Order create(@RequestBody CreateOrderDto request) {
        return orderService.create(request);
    }

    @PatchMapping(ORDER_CHANGE_DESTINATION_URL)
    private Order changeDestination(@RequestBody ChangeOrderDestinationDto request) {
        return orderService.changeDestination(request);
    }

}
