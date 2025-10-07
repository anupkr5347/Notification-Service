package com.build.project.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    String type;
    String recipientId;
    String title;
    private Map<String, Object> context;
}
