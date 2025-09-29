package com.build.project.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> ops;
    public PresenceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForValue();
    }

    public void markOnline(String userId, String sessionId) {
        ops.set("presence:user:" + userId, sessionId);
    }

    public void markOffline(String userId) {
        redisTemplate.delete("presence:user:" + userId);
    }

    public String getSessionForUser(String userId) {
        return ops.get("presence:user:" + userId);
    }
}
