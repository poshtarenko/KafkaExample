package com.example.reportsservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${topic-names.order-creation}")
    private String orderCreationTopic;

    @Bean
    public NewTopic orderCreationTopic() {
        return TopicBuilder.name(orderCreationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

}