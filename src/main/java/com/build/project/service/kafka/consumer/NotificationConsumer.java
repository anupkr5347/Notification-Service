package com.build.project.service.kafka.consumer;

import com.build.project.constant.NotificationConstant;
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
                String sessionId = presenceService.getSessionForUser(notification.getRecipientId());
                if (sessionId == null) {
                    sessionId = presenceService.createSessionForUser(notification.getRecipientId());
                }
                messagingTemplate.convertAndSendToUser(notification.getRecipientId(), "/queue/notifications", notification);
                notification.setStatus(NotificationConstant.SENT);
                notification.setDeliveredAt(Instant.now());
            }
            repo.save(notification);
        } catch (Exception ex) {
            // increment attempts, possibly send to DLQ
            notification.setAttempts(notification.getAttempts() + 1);
            notification.setStatus(NotificationConstant.FAILED);
            repo.save(notification);
            // Ideally publish to DLQ or handle retries via Kafka retry logic
        }
    }
}