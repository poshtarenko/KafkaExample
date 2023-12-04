package com.example.ordersservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicConfig {

    @Value("${topic-names.order}")
    private String topicName;

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .build();
    }
}