package com.example.reportsservice.repository;

import com.example.reportsservice.domain.ProductSalesReport;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ProductSalesReportRepository extends CrudRepository<ProductSalesReport, String> {
}
