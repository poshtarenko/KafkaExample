package com.example.reportsservice.controller.api;

import com.example.reportsservice.domain.ProductSalesReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface ReportsAPI {

    @Operation(summary = "Get report by product name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found the report",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductSalesReport.class))}),
            @ApiResponse(
                    responseCode = "500", description = "Server error",
                    content = @Content)})
    ProductSalesReport get(String productName);

    @Operation(summary = "Get report by product name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Downloaded the report file",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = byte[].class))}),
            @ApiResponse(
                    responseCode = "500", description = "Server error",
                    content = @Content)})
    ResponseEntity<ByteArrayResource> downloadReportFile(String productName);

}
