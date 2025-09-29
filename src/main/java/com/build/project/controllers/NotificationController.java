package com.build.project.controllers;

import com.build.project.service.kafka.producer.NotificationProducer;
import com.build.project.model.Notification;
import com.build.project.repository.NotificationRepository;
import com.build.project.request.NotificationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository repo;
    private final NotificationProducer producer;

    public NotificationController(NotificationRepository repo, NotificationProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<?> send(@RequestBody NotificationRequest req) {
        Notification notification = new Notification();
        notification.setRecipientId(req.getRecipientId());
        notification.setType(req.getType());
        notification.setTitle(req.getTitle());
        notification.setBody(req.getBody());
        notification.setStatus("PENDING");
        notification.setPayload("MyPayload");
        repo.save(notification);
        producer.publish(notification);
        return ResponseEntity.accepted().body(Map.of("id", notification.getId()));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody NotificationRequest req) {
        req = new NotificationRequest(req.getType(), req.getRecipientId(), req.getTitle(), req.getBody());
        return send(req);
    }
}
