package com.example.ordersservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

//    @Value("${topic-names.order-creation}")
//    private String orderCreationTopic;
//    @Value("${topic-names.change-order-destination}")
//    private String changeOrderDestinationTopic;
//    @Value("${topic-names.update-order-status}")
//    private String updateOrderStatusTopic;
//
//    @Bean
//    public NewTopic orderCreationTopic() {
//        return TopicBuilder.name(orderCreationTopic)
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic changeOrderDestinationTopic() {
//        return TopicBuilder.name(changeOrderDestinationTopic)
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic updateOrderStatusTopic() {
//        return TopicBuilder.name(updateOrderStatusTopic)
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }

}