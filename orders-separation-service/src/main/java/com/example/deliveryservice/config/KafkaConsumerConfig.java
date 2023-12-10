package com.example.deliveryservice.config;

import com.example.common.messaging.models.ChangeOrderDestinationEvent;
import com.example.common.messaging.models.OrderCreationEvent;
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
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.List;

@Configuration
//@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private List<String> bootstrapServers;
//
//    @Value("${topics.order.name-retry}")
//    private String retryTopic;
//
//    @Value("${topics.order.name-dlt}")
//    private String deadLetterTopic;

    @Bean
    public ConsumerFactory<String, OrderCreationEvent> orderEventConsumerFactory(KafkaProperties kafkaProperties) {
        //noinspection removal
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderCreationEvent>>
    kafkaOrderEventListenerContainerFactory(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderEventConsumerFactory(kafkaProperties));
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChangeOrderDestinationEvent> changeOrderDestinationEventConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        //noinspection removal
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ChangeOrderDestinationEvent>>
    changeOrderDestinationEventListenerContainerFactory(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, ChangeOrderDestinationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(changeOrderDestinationEventConsumerFactory(kafkaProperties));
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    public DefaultErrorHandler errorHandler() {
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(2);
        backOff.setInitialInterval(1000L);
        backOff.setMaxInterval(2000L);
        backOff.setMultiplier(2);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);

        List<Class<? extends Exception>> ignorableExceptions = List.of(IllegalArgumentException.class);
        ignorableExceptions.forEach(errorHandler::addNotRetryableExceptions);
        List<Class<? extends Exception>> retryableExceptions = List.of(QueryTimeoutException.class);
        retryableExceptions.forEach(errorHandler::addRetryableExceptions);
        return errorHandler;
    }

//    public DeadLetterPublishingRecoverer publishingRecoverer() {
//        return new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, e) -> {
//            if (e.getCause() instanceof QueryTimeoutException) {
//                return new TopicPartition(retryTopic, consumerRecord.partition());
//            } else {
//                return new TopicPartition(deadLetterTopic, consumerRecord.partition());
//            }
//        });
//    }


}
