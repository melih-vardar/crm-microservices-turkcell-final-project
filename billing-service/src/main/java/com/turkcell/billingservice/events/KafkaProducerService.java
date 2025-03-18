package com.turkcell.billingservice.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, BillCreatedEvent> kafkaTemplate;
    private static final String TOPIC_BILL_CREATED = "bill-created";

    public void sendBillCreatedEvent(BillCreatedEvent event) {
        kafkaTemplate.send(TOPIC_BILL_CREATED, event);
    }
} 