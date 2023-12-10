package com.example.deliveryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${topic-names.update-order-status}")
    private String updateOrderStatusTopic;

    @Bean
    public NewTopic updateOrderStatusTopic() {
        return TopicBuilder.name(updateOrderStatusTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

}