package com.example.reportsservice.controller;

import com.example.reportsservice.controller.api.ReportsAPI;
import com.example.reportsservice.domain.ProductSalesReport;
import com.example.reportsservice.service.ProductSalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ReportsController implements ReportsAPI {

    private static final String GET_REPORT_DATA_URL = "/report-data";
    private static final String GET_REPORT_FILE_URL = "/report-file";

    private final ProductSalesReportService productSalesReportService;

    @GetMapping(GET_REPORT_DATA_URL + "/{productName}")
    public ProductSalesReport get(@PathVariable String productName) {
        return productSalesReportService.get(productName);
    }

    @GetMapping(GET_REPORT_FILE_URL + "/{productName}")
    public ResponseEntity<ByteArrayResource> downloadReportFile(@PathVariable String productName) {
        byte[] file = productSalesReportService.getReportFile(productName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(file));
    }

}
