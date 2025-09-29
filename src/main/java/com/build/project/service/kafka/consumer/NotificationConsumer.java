package com.build.project.service.kafka.consumer;

import com.build.project.model.Notification;
import com.build.project.repository.NotificationRepository;
import com.build.project.service.PresenceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository repo;
    private final PresenceService presenceService; // wraps Redis presence checks
    public NotificationConsumer(SimpMessagingTemplate messagingTemplate, NotificationRepository repo, PresenceService presenceService) {
        this.messagingTemplate = messagingTemplate;
        this.repo = repo;
        this.presenceService = presenceService;
    }

    @KafkaListener(topics = "notifications", groupId = "notification-consumer-group")
    public void onMessage(Notification notification) {
        // Simple delivery logic
        try {
            if (notification.getRecipientId() == null) {
                // broadcast
                messagingTemplate.convertAndSend("/topic/notifications", notification);
            } else {
                // if recipient is online (checked via Redis), deliver to user queue, else mark for fallback
                String sessionId = presenceService.getSessionForUser(notification.getRecipientId());
                if (sessionId != null) {
                    // send to user-specific destination
                    messagingTemplate.convertAndSendToUser(notification.getRecipientId(), "/queue/notifications", notification);
                    notification.setStatus("SENT");
                    notification.setDeliveredAt(Instant.now());
                } else {
                    notification.setStatus("PENDING"); // still pending, will later fallback
                }
            }
            repo.save(notification);
        } catch (Exception ex) {
            // increment attempts, possibly send to DLQ
            notification.setAttempts(notification.getAttempts() + 1);
            notification.setStatus("FAILED");
            repo.save(notification);
            // Ideally publish to DLQ or handle retries via Kafka retry logic
        }
    }
}