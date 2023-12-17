package com.example.ordersservice.config;

import com.example.common.messaging.models.UpdateOrderStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.List;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

//    @Bean
//    public ConsumerFactory<String, UpdateOrderStatusEvent> consumerFactory(KafkaProperties kafkaProperties) {
//        //noinspection removal
//        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
//    }
//
//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, UpdateOrderStatusEvent>>
//    containerFactory(KafkaProperties kafkaProperties) {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory(kafkaProperties));
//        factory.setCommonErrorHandler(errorHandler());
//        return factory;
//    }
//
//    public DefaultErrorHandler errorHandler() {
//        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(2);
//        backOff.setInitialInterval(1000L);
//        backOff.setMaxInterval(2000L);
//        backOff.setMultiplier(2);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);
//
//        List<Class<? extends Exception>> ignorableExceptions = List.of(IllegalArgumentException.class);
//        ignorableExceptions.forEach(errorHandler::addNotRetryableExceptions);
//        List<Class<? extends Exception>> retryableExceptions = List.of(QueryTimeoutException.class);
//        retryableExceptions.forEach(errorHandler::addRetryableExceptions);
//        return errorHandler;
//    }


}
