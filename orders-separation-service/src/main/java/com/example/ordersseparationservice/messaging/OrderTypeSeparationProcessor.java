package com.example.ordersseparationservice.messaging;

import com.example.common.messaging.models.OrderCreationEvent;
import com.example.ordersseparationservice.domain.OrderType;
import com.example.ordersseparationservice.service.OrderTypeService;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTypeSeparationProcessor {

    @Value(value = "${topic-names.order-creation}")
    private String orderCreationTopic;
    @Value(value = "${topic-names.cheap-order-creation}")
    private String cheapOrderCreationTopic;
    @Value(value = "${topic-names.expensive-order-creation}")
    private String expensiveOrderCreationTopic;

    private final SpecificAvroSerde<OrderCreationEvent> orderCreationEventSerde;

    private final OrderTypeService orderTypeService;

    public KStream<String, OrderCreationEvent>[] buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, OrderCreationEvent> orderStream = streamsBuilder
                .stream(orderCreationTopic, Consumed.with(Serdes.String(), orderCreationEventSerde))
                .peek((key, event) -> log.info("Processing event : {}", event));

        Predicate<String, OrderCreationEvent> isExpensiveOrder = (key, event) ->
                OrderType.EXPENSIVE.equals(orderTypeService.defineOrderType(event));
        Predicate<String, OrderCreationEvent> isCheapOrder = (key, event) ->
                OrderType.CHEAP.equals(orderTypeService.defineOrderType(event));

        KStream<String, OrderCreationEvent>[] branches = orderStream.branch(isCheapOrder, isExpensiveOrder);

        branches[0]
                .peek((key, event) -> log.info("Sending cheap order creation event : {}", event))
                .to(cheapOrderCreationTopic);
        branches[1]
                .peek((key, event) -> log.info("Sending expensive order creation event : {}", event))
                .to(expensiveOrderCreationTopic);

        return branches;
    }

}