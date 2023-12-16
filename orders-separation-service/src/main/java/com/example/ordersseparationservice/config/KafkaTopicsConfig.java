package com.example.ordersseparationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${topic-names.order-creation}")
    private String orderCreationTopic;
    @Value(value = "${topic-names.cheap-order-creation}")
    private String cheapOrderCreationTopic;
    @Value(value = "${topic-names.expensive-order-creation}")
    private String expensiveOrderCreationTopic;

    @Bean
    public NewTopic orderCreationTopic() {
        return TopicBuilder.name(orderCreationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic cheapOrderCreationTopic() {
        return TopicBuilder.name(cheapOrderCreationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic expensiveOrderCreationTopic() {
        return TopicBuilder.name(expensiveOrderCreationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

}