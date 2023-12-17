package com.example.deliveryservice.messaging.listener;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.common.messaging.models.OrderStatus;
import com.example.deliveryservice.service.DeliveryService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EmbeddedKafka(topics = {"cheap-order-creation"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.properties.schema.registry.url=mock://localhost:8081",
        "retryListener.startup=false"})
class OrderEventListenerITest {

    @SpyBean
    private OrderEventListener orderEventListener;
    @SpyBean
    private DeliveryService deliveryService;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;
    private KafkaProducer<Object, Object> producer;

    @Value("${spring.embedded.kafka.brokers}")
    private String kafkaBrokers;
    @Value("${topic-names.cheap-order-creation}")
    private String topicName;
    @Value("${topic-consumer-groups.cheap-order-creation}")
    private String groupId;

    @BeforeEach
    void setUp() {
        List<MessageListenerContainer> containers = endpointRegistry.getAllListenerContainers().stream()
                .filter(container -> Objects.equals(container.getGroupId(), groupId))
                .toList();
        for (MessageListenerContainer container : containers) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
        producer = createProducer();
    }

    @AfterEach
    void tearDown() {
        producer.close();
    }

    @Test
    void onOrderEvent() throws Exception {
        OrderCreationEvent event = OrderCreationEvent.newBuilder()
                .setId(1L)
                .setProduct("product")
                .setCategory("category")
                .setCustomerName("name")
                .setPrice("20.00")
                .setStatus(OrderStatus.CREATED)
                .setDeliveryDestination("dest")
                .setIsCompleted(false)
                .setCompletedAt(LocalDateTime.now())
                .build();
        ProducerRecord<Object, Object> record = new ProducerRecord<>(topicName, event);
        producer.send(record).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
        verify(orderEventListener, times(1)).onCheapOrderCreationEvent(captor.capture(), any());
        assertOrderEvent(event, captor.getValue());
    }

    private KafkaProducer<Object, Object> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://localhost:8081");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "kafkatest");
        return new KafkaProducer<>(props);
    }

//    @Test
//    void onOrderEventWhenValidationExceptionShouldNotMakeAdditionalRetries() throws Exception {
//        OrderCreationEvent event = OrderCreationEvent.newBuilder()
//                .setId(1L)
//                .setCustomerName("name")
//                .setDeliveryDestination("")
//                .build();
//        kafkaTemplate.send(topicName, event).get();
//
//        CountDownLatch latch = new CountDownLatch(1);
//        latch.await(2, TimeUnit.SECONDS);
//
//        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
//        verify(orderEventListener, times(1)).onExpensiveOrderCreationEvent(captor.capture(), any());
//        assertOrderEvent(event, captor.getValue());
//    }
//
//    @Test
//    void onOrderEventWhenDatabaseConnectionExceptionShouldMake3Retries() throws Exception {
//        doThrow(new QueryTimeoutException("Connection exception")).when(deliveryService).startDelivery(any());
//        OrderCreationEvent event = OrderCreationEvent.newBuilder()
//                .setId(1L)
//                .setCustomerName("name")
//                .setDeliveryDestination("dest")
//                .build();
//        kafkaTemplate.send(topicName, event).get();
//
//        CountDownLatch latch = new CountDownLatch(1);
//        latch.await(4, TimeUnit.SECONDS);
//
//        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
//        verify(orderEventListener, times(3)).onExpensiveOrderCreationEvent(captor.capture(), any());
//        verify(deliveryService, times(3)).startDelivery(any());
//        assertOrderEvent(event, captor.getValue());
//
//        Map<String, Object> props = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
//        Consumer<String, OrderCreationEvent> consumer = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
//                new JsonDeserializer<>(OrderCreationEvent.class)).createConsumer();
//        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, retryTopic);
//
//        ConsumerRecord<String, OrderCreationEvent> retryRecord = KafkaTestUtils.getSingleRecord(consumer, retryTopic);
//        assertOrderEvent(event, retryRecord.value());
//    }

    private void assertOrderEvent(OrderCreationEvent expected, OrderCreationEvent actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCustomerName(), actual.getCustomerName().toString());
        assertEquals(expected.getDeliveryDestination(), actual.getDeliveryDestination().toString());
    }

}