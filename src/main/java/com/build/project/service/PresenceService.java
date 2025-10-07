package com.build.project.service;

import com.build.project.constant.NotificationConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class PresenceService {
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> ops;
    public PresenceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForValue();
    }

    public void markOnline(String userId, String sessionId) {
        ops.set(NotificationConstant.PRESENCE_USER + userId, sessionId);
    }

    public void markOffline(String userId) {
        redisTemplate.delete(NotificationConstant.PRESENCE_USER + userId);
    }

    public String getSessionForUser(String userId) {
        return ops.get(NotificationConstant.PRESENCE_USER + userId);
    }

    public String createSessionForUser(String userId) {
        String sessionId = UUID.randomUUID().toString();
        String key = NotificationConstant.PRESENCE_USER + userId;
        ops.set(key, sessionId, Duration.ofMinutes(30));
        return sessionId;
    }
}
