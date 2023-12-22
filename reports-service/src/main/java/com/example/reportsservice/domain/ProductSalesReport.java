package com.example.reportsservice.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "ProductSalesReports")
@ToString
public class ProductSalesReport {

    String productName;

    Integer salesCount;

    List<String> customers;

    String reportFileKey;

    @DynamoDBHashKey
    public String getProductName() {
        return productName;
    }

    @DynamoDBAttribute
    public Integer getSalesCount() {
        return salesCount;
    }

    @DynamoDBAttribute
    public List<String> getCustomers() {
        return customers;
    }

    @DynamoDBAttribute
    public String getReportFileKey() {
        return reportFileKey;
    }

}
