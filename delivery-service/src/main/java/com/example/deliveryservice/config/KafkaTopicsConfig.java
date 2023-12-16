package com.example.deliveryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value(value = "${topic-names.cheap-order-creation}")
    private String cheapOrderCreationTopic;
    @Value(value = "${topic-names.expensive-order-creation}")
    private String expensiveOrderCreationTopic;
    @Value("${topic-names.update-order-status}")
    private String updateOrderStatusTopic;
    @Value("${topic-names.change-order-destination}")
    private String changeOrderDestinationTopic;

    @Bean
    public NewTopic updateOrderStatusTopic() {
        return TopicBuilder.name(updateOrderStatusTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic changeOrderDestinationTopic() {
        return TopicBuilder.name(changeOrderDestinationTopic)
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