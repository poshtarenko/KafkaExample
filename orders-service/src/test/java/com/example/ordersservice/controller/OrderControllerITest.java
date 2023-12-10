package com.example.ordersservice.controller;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.dto.CreateOrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(topics = "${topic-names.order-creation}")
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class OrderControllerITest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, OrderCreationEvent> consumer;

    @Value("${topic-names.order-creation}")
    private String orderCreationTopic;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OrderCreationEvent.class))
                .createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, orderCreationTopic);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Autowired
    private ObjectMapper objectMapper;

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

        ConsumerRecords<String, OrderCreationEvent> records = KafkaTestUtils.getRecords(consumer);
        assertEquals(1, records.count());
        OrderCreationEvent record = records.iterator().next().value();
        assertEquals(record.getId(), response.getId());
        assertEquals(record.getCustomerName(), response.getCustomerName());
        assertEquals(record.getDeliveryDestination(), response.getDeliveryDestination());
    }

}