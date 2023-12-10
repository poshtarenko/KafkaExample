package com.example.deliveryservice.messaging.listener;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.deliveryservice.service.DeliveryService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.dao.QueryTimeoutException;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EmbeddedKafka(topics = {"order", "order.RETRY", "order.DLT"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "retryListener.startup=false"})
class OrderEventListenerITest {

    @SpyBean
    private OrderEventListener orderEventListener;
    @SpyBean
    private DeliveryService deliveryService;
    @Autowired
    private KafkaTemplate<String, OrderCreationEvent> kafkaTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    @Value("${spring.kafka.consumer.group-id}")
    private String topicGroupId;
    @Value("${topics.order.name}")
    private String topicName;
    @Value("${topics.order.name-retry}")
    private String retryTopic;
    @Value("${topics.order.name-dlt}")
    private String deadLetterTopic;

    @BeforeEach
    void setUp() {
        List<MessageListenerContainer> containers = endpointRegistry.getAllListenerContainers().stream()
                .filter(container -> Objects.equals(container.getGroupId(), topicGroupId))
                .toList();
        for (MessageListenerContainer container : containers) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }

    }

    @Test
    void onOrderEvent() throws Exception {
        OrderCreationEvent event = OrderCreationEvent.newBuilder()
                .setId(1L)
                .setCustomerName("name")
                .setDeliveryDestination("dest")
                .build();
        kafkaTemplate.send(topicName, event).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
        verify(orderEventListener, times(1)).onOrderCreationEvent(captor.capture(), any());
        assertOrderEvent(event, captor.getValue());
    }

    @Test
    void onOrderEventWhenValidationExceptionShouldNotMakeAdditionalRetries() throws Exception {
        OrderCreationEvent event = OrderCreationEvent.newBuilder()
                .setId(1L)
                .setCustomerName("name")
                .setDeliveryDestination("")
                .build();
        kafkaTemplate.send(topicName, event).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2, TimeUnit.SECONDS);

        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
        verify(orderEventListener, times(1)).onOrderCreationEvent(captor.capture(), any());
        assertOrderEvent(event, captor.getValue());
    }

    @Test
    void onOrderEventWhenDatabaseConnectionExceptionShouldMake3Retries() throws Exception {
        doThrow(new QueryTimeoutException("Connection exception")).when(deliveryService).startDelivery(any());
        OrderCreationEvent event = OrderCreationEvent.newBuilder()
                .setId(1L)
                .setCustomerName("name")
                .setDeliveryDestination("dest")
                .build();
        kafkaTemplate.send(topicName, event).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(4, TimeUnit.SECONDS);

        ArgumentCaptor<OrderCreationEvent> captor = ArgumentCaptor.forClass(OrderCreationEvent.class);
        verify(orderEventListener, times(3)).onOrderCreationEvent(captor.capture(), any());
        verify(deliveryService, times(3)).startDelivery(any());
        assertOrderEvent(event, captor.getValue());

        Map<String, Object> props = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        Consumer<String, OrderCreationEvent> consumer = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(OrderCreationEvent.class)).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, retryTopic);

        ConsumerRecord<String, OrderCreationEvent> retryRecord = KafkaTestUtils.getSingleRecord(consumer, retryTopic);
        assertOrderEvent(event, retryRecord.value());
    }

    private void assertOrderEvent(OrderCreationEvent expected, OrderCreationEvent actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCustomerName(), actual.getCustomerName());
        assertEquals(expected.getDeliveryDestination(), actual.getDeliveryDestination());
    }

}