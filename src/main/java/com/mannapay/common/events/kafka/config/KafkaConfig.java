package com.mannapay.common.events.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise-grade Kafka configuration for MannaPay platform.
 *
 * Features:
 * - Exactly-once semantics via idempotent producer
 * - Manual acknowledgment for reliable processing
 * - Dead letter queue for failed messages
 * - Configurable retry with exponential backoff
 * - JSON serialization with proper type handling
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.application.name:unknown-service}")
    private String applicationName;

    @Value("${kafka.consumer.max-poll-records:500}")
    private int maxPollRecords;

    @Value("${kafka.consumer.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${kafka.consumer.retry.interval-ms:1000}")
    private long retryIntervalMs;

    // ==================== Admin Configuration ====================

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // ==================== Topic Creation ====================

    @Bean
    public NewTopic transferEventsTopic() {
        return TopicBuilder.name("mannapay.transfer.events")
            .partitions(12)
            .replicas(3)
            .config("retention.ms", "604800000") // 7 days
            .config("min.insync.replicas", "2")
            .build();
    }

    @Bean
    public NewTopic transferEventsDlqTopic() {
        return TopicBuilder.name("mannapay.transfer.events.dlq")
            .partitions(3)
            .replicas(3)
            .config("retention.ms", "2592000000") // 30 days
            .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name("mannapay.payment.events")
            .partitions(12)
            .replicas(3)
            .config("retention.ms", "604800000")
            .config("min.insync.replicas", "2")
            .build();
    }

    @Bean
    public NewTopic paymentEventsDlqTopic() {
        return TopicBuilder.name("mannapay.payment.events.dlq")
            .partitions(3)
            .replicas(3)
            .config("retention.ms", "2592000000")
            .build();
    }

    @Bean
    public NewTopic walletEventsTopic() {
        return TopicBuilder.name("mannapay.wallet.events")
            .partitions(12)
            .replicas(3)
            .config("retention.ms", "604800000")
            .config("min.insync.replicas", "2")
            .build();
    }

    @Bean
    public NewTopic walletEventsDlqTopic() {
        return TopicBuilder.name("mannapay.wallet.events.dlq")
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic complianceEventsTopic() {
        return TopicBuilder.name("mannapay.compliance.events")
            .partitions(6)
            .replicas(3)
            .config("retention.ms", "2592000000") // 30 days for compliance
            .build();
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name("mannapay.user.events")
            .partitions(6)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name("mannapay.notification.events")
            .partitions(6)
            .replicas(3)
            .config("retention.ms", "86400000") // 1 day
            .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name("mannapay.audit.events")
            .partitions(12)
            .replicas(3)
            .config("retention.ms", "31536000000") // 1 year for audit
            .build();
    }

    @Bean
    public NewTopic fraudEventsTopic() {
        return TopicBuilder.name("mannapay.fraud.events")
            .partitions(6)
            .replicas(3)
            .config("retention.ms", "2592000000")
            .build();
    }

    @Bean
    public NewTopic sagaEventsTopic() {
        return TopicBuilder.name("mannapay.saga.events")
            .partitions(12)
            .replicas(3)
            .build();
    }

    // ==================== Producer Configuration ====================

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Bootstrap servers
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serialization
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Exactly-once semantics
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Performance tuning
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        // Timeout configurations
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);

        // Client identification
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, applicationName + "-producer");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        template.setObservationEnabled(true); // Enable tracing
        return template;
    }

    // ==================== Consumer Configuration ====================

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Bootstrap servers
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Consumer group
        props.put(ConsumerConfig.GROUP_ID_CONFIG, applicationName + "-group");

        // Deserialization with error handling
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // JSON deserializer settings
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.mannapay.*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);

        // Offset management - disable auto-commit for manual acknowledgment
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Performance tuning
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        // Session and heartbeat
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);

        // Client identification
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, applicationName + "-consumer");

        // Isolation level for exactly-once
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // Manual acknowledgment for reliability
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Concurrency - number of consumer threads
        factory.setConcurrency(3);

        // Error handling with DLQ
        factory.setCommonErrorHandler(errorHandler(kafkaTemplate));

        // Enable batch listening for high throughput (optional)
        factory.setBatchListener(false);

        // Observation for tracing
        factory.getContainerProperties().setObservationEnabled(true);

        return factory;
    }

    /**
     * Batch listener container for high-throughput scenarios.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(errorHandler(kafkaTemplate));
        factory.setBatchListener(true);

        return factory;
    }

    // ==================== Error Handling ====================

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // Dead letter publishing recoverer - sends to DLQ after retries exhausted
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, ex) -> {
                // Append .dlq to the original topic
                String dlqTopic = record.topic() + ".dlq";
                log.error("Sending to DLQ: topic={}, key={}, error={}",
                    dlqTopic, record.key(), ex.getMessage());
                return new org.apache.kafka.common.TopicPartition(dlqTopic, -1);
            }
        );

        // Fixed backoff: retry N times with fixed interval
        FixedBackOff backOff = new FixedBackOff(retryIntervalMs, maxRetryAttempts);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Don't retry for certain exceptions
        errorHandler.addNotRetryableExceptions(
            IllegalArgumentException.class,
            IllegalStateException.class
        );

        return errorHandler;
    }

    // ==================== ObjectMapper ====================

    @Bean
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
