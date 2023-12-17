package com.example.ordersservice.controller;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.dto.CreateOrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(topics = "${topic-names.order-creation}")
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.properties.schema.registry.url=mock://localhost:8081"})
class OrderControllerITest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private ObjectMapper objectMapper;

    private KafkaConsumer<Object, Object> consumer;

    @Value("${spring.embedded.kafka.brokers}")
    private String kafkaBrokers;
    @Value("${topic-names.order-creation}")
    private String orderCreationTopic;

    @BeforeEach
    void setUp() {
        consumer = createEventConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, orderCreationTopic);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void createOrder() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto("Milk", "Milk products", BigDecimal.valueOf(20, 20), "Customer name", "Kyiv 012321");

        MvcResult mvcResult = mockMvc.perform(post("/orders/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isOk())
                .andReturn();
        Order response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Order.class);

        assertNotNull(response);

        ConsumerRecords<Object, Object> records = KafkaTestUtils.getRecords(consumer);
        assertEquals(1, records.count());
        OrderCreationEvent record = (OrderCreationEvent) records.iterator().next().value();
        assertEquals(record.getId(), response.getId());
        assertEquals(record.getCustomerName().toString(), response.getCustomerName());
        assertEquals(record.getDeliveryDestination().toString(), response.getDeliveryDestination());
    }

    private KafkaConsumer<Object, Object> createEventConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkatest");
        return new KafkaConsumer<>(props);
    }

}