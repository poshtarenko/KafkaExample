package com.example.deliveryservice.config;

import com.example.common.messaging.models.UpdateOrderStatusEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, UpdateOrderStatusEvent> updateOrderStatusEventProducerFactory(KafkaProperties kafkaProperties) {
        //noinspection removal
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put(ProducerConfig.ACKS_CONFIG, "all");
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configs.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "delivery-service-transactional-id-1");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, UpdateOrderStatusEvent> kafkaUpdateOrderStatusEventTemplate(
            ProducerFactory<String, UpdateOrderStatusEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
