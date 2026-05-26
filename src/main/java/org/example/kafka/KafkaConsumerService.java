package org.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * =====================================================================
 *  KafkaConsumerService: Asynchronous Message Listener
 * =====================================================================
 * Subscribes to the Kafka topic and processes incoming JSON payloads.
 * Demonstrates extracting core Kafka metadata (Key, Partition, Offset).
 */
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Listens to the target topic and automatically deserializes the incoming
     * JSON bytes back into a CustomMessage POJO.
     * 
     * 🧠 METADATA EXTRACTION:
     * Using Spring Messaging headers, we can extract important Kafka partition
     * level metadata for logging or state tracking.
     * 
     * @param message Deserialized JSON payload
     * @param partition Partition the message was read from
     * @param offset Logical offset of the record
     * @param key Message key used during routing
     */
    @KafkaListener(
            topics = KafkaConfig.TOPIC_NAME,
            groupId = "demo-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMessage(
            @Payload CustomMessage message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info("📩 Message Received successfully!");
        log.info("   [Metadata] Topic: {}, Partition: {}, Offset: {}, Key: '{}'", 
                KafkaConfig.TOPIC_NAME, partition, offset, key);
        log.info("   [Payload] {}", message);

        // Here you would execute your business logic (e.g. database saving, downstream triggers)
    }
}
