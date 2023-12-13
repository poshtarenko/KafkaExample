package com.example.ordersseparationservice.config;

import com.example.common.messaging.models.OrderCreationEvent;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
public class KafkaSerdeConfig {

    @Value(value = "${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryURL;

    @Bean
    public SpecificAvroSerde<OrderCreationEvent> orderCreationEventSerde() {
        SpecificAvroSerde<OrderCreationEvent> serde = new SpecificAvroSerde<>();
        boolean isKeySerde = false;
        Map<String, String> schemaRegistryProps = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                schemaRegistryURL);
        serde.configure(schemaRegistryProps, isKeySerde);
        return serde;
    }

}
