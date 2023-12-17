package com.example.ordersservice.config;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
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

//    @Bean
//    public ProducerFactory<String, OrderCreationEvent> orderCreationEventProducerFactory(KafkaProperties kafkaProperties) {
//        //noinspection removal
//        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
//        configs.put(ProducerConfig.ACKS_CONFIG, "all");
//        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        configs.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-service-transactional-id-1");
//        return new DefaultKafkaProducerFactory<>(configs);
//    }
//
//    @Bean
//    public ProducerFactory<String, ChangeOrderDestinationEvent> changeOrderDestinationEventProducerFactory(KafkaProperties kafkaProperties) {
//        //noinspection removal
//        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
//        configs.put(ProducerConfig.ACKS_CONFIG, "all");
//        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        configs.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-service-transactional-id-2");
//        return new DefaultKafkaProducerFactory<>(configs);
//    }
//
//
//    @Bean
//    public KafkaTemplate<String, OrderCreationEvent> kafkaOrderEventTemplate(
//            ProducerFactory<String, OrderCreationEvent> producerFactory
//    ) {
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//    @Bean
//    public KafkaTemplate<String, ChangeOrderDestinationEvent> kafkaChangeOrderDestinationTemplate(
//            ProducerFactory<String, ChangeOrderDestinationEvent> producerFactory
//    ) {
//        return new KafkaTemplate<>(producerFactory);
//    }

}
