package com.mannapay.common.events.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Metadata for event processing and auditing.
 *
 * Contains information needed for:
 * - Distributed tracing
 * - Audit logging
 * - Regulatory compliance
 * - Performance monitoring
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tracing
    private String traceId;
    private String spanId;
    private String parentSpanId;

    // Request context
    private String requestId;
    private String sessionId;
    private String clientIp;
    private String userAgent;

    // User context
    private String userId;
    private String userName;
    private String userEmail;
    private String userRole;

    // Service context
    private String serviceName;
    private String serviceVersion;
    private String serviceInstance;
    private String environment;
    private String region;

    // Processing info
    private Instant processingStartTime;
    private Instant processingEndTime;
    private Long processingDurationMs;

    // Additional attributes
    private Map<String, String> attributes;

    /**
     * Calculate processing duration.
     */
    public void completeProcessing() {
        this.processingEndTime = Instant.now();
        if (processingStartTime != null) {
            this.processingDurationMs =
                processingEndTime.toEpochMilli() - processingStartTime.toEpochMilli();
        }
    }

    /**
     * Start processing timer.
     */
    public void startProcessing() {
        this.processingStartTime = Instant.now();
    }

    /**
     * Add attribute.
     */
    public void addAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    /**
     * Get attribute.
     */
    public String getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }

    /**
     * Create from current context.
     */
    public static EventMetadata fromContext(String serviceName, String userId) {
        return EventMetadata.builder()
            .serviceName(serviceName)
            .userId(userId)
            .processingStartTime(Instant.now())
            .attributes(new HashMap<>())
            .build();
    }
}
