package com.example.reportsservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories
  (basePackages = "com.example.reportsservice.repository")
public class DynamoDBConfig {

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSCredentials credentials) {
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(credentials);
        amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
        return amazonDynamoDB;
    }

}