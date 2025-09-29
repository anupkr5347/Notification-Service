package com.build.project.service.kafka.producer;

import com.build.project.model.Notification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public static final String TOPIC = "notifications";

    public NotificationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Notification notification) {
        // key = recipientId or "broadcast"
        String key = notification.getRecipientId() == null ? "broadcast" : notification.getRecipientId();
        kafkaTemplate.send(TOPIC, key, notification);
    }
}
