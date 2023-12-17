package com.example.ordersservice.controller.api;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.dto.ChangeOrderDestinationDto;
import com.example.ordersservice.dto.CreateOrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface OrderAPI {

    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found the order",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(
                    responseCode = "500", description = "Server error",
                    content = @Content)})
    Order get(Long id);

    @Operation(summary = "Create order")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Order created",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(
                    responseCode = "500", description = "Server error",
                    content = @Content)})
    Order create(CreateOrderDto request);

    @Operation(summary = "Change order destination")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Order destination changed",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(
                    responseCode = "500", description = "Server error",
                    content = @Content)})
    Order changeDestination(ChangeOrderDestinationDto request);

}
