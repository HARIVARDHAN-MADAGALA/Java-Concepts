package org.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * =====================================================================
 *  KafkaConfig: Complete Spring Boot Apache Kafka Configuration
 * =====================================================================
 * Declares the core configuration components to enable high-performance
 * message publishing and consumption using custom JSON Serialization.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String TOPIC_NAME = "demo-topic";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    // =================================================================
    //  1. TOPIC PROVISIONING (Spring Admin)
    // =================================================================
    /**
     * Automatically registers a new topic on the Kafka cluster at startup if it
     * doesn't already exist.
     */
    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(TOPIC_NAME)
                .partitions(3)         // 3 partitions for horizontal scaling / parallelism
                .replicas(1)           // 1 replica (local development default)
                .build();
    }

    // =================================================================
    //  2. PRODUCER CONFIGURATION
    // =================================================================
    /**
     * The ProducerFactory sets up the underlying Kafka producer connection pool.
     * We configure key/value serializers, enabling JSON serialization for payloads.
     */
    @Bean
    public ProducerFactory<String, CustomMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // ⚡ PRODUCER PERFORMANCE TUNING (Batching & Reliability):
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");          // Wait for all replica logs (maximum safety)
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);           // Retry sending message up to 3 times on transient failure
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // Guarantee exact-once delivery per message batch
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);    // Buffer up to 16KB of messages before sending (throughput)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);         // Wait 5ms for extra messages before flushing batches (lowers network load)

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * The wrapper template used in our service code to produce messages.
     */
    @Bean
    public KafkaTemplate<String, CustomMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // =================================================================
    //  3. CONSUMER CONFIGURATION
    // =================================================================
    /**
     * The ConsumerFactory sets up the underlying Kafka consumer connection pool.
     */
    @Bean
    public ConsumerFactory<String, CustomMessage> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Custom JsonDeserializer configuration to prevent security vulnerabilities (requires trusted packages)
        JsonDeserializer<CustomMessage> jsonDeserializer = new JsonDeserializer<>(CustomMessage.class);
        jsonDeserializer.addTrustedPackages("org.example.kafka"); // Trust our package structure for deserialization
        jsonDeserializer.setUseTypeHeaders(false);                 // Simplify mapping directly to our class structure

        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // If group has no offset, start from beginning

        // ⚡ CONSUMER PERFORMANCE TUNING:
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");   // Auto-commit offset reads every interval
        configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000); // Commit offset every 1 second

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    /**
     * Declares the listener container factory required by Spring's @KafkaListener annotations.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CustomMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
