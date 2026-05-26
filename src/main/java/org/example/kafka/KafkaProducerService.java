package org.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * =====================================================================
 *  KafkaProducerService: Production-grade Message Publisher
 * =====================================================================
 * Demonstrates high-performance asynchronous message production using
 * Spring's KafkaTemplate.
 */
@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, CustomMessage> kafkaTemplate;

    /**
     * Publishes a message asynchronously to Kafka.
     * 
     * 🧠 ASYNCHRONOUS CALLBACKS:
     * When we call kafkaTemplate.send(), the message is buffered locally in memory
     * and handled by the Producer's background Sender thread. 
     * The send() method immediately returns a CompletableFuture. By attaching
     * callbacks (.whenComplete), we avoid blocking the calling thread while
     * still logging success or handling connection errors.
     * 
     * @param key Message key (Guarantees ordering inside Kafka partitions)
     * @param message CustomMessage payload
     */
    public void sendMessage(String key, CustomMessage message) {
        log.info("Sending message with Key: '{}' and Payload: {}", key, message);

        // Asynchronous send operation
        CompletableFuture<SendResult<String, CustomMessage>> future =
                kafkaTemplate.send(KafkaConfig.TOPIC_NAME, key, message);

        // Register async completion callbacks
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Success path
                log.info("✅ Message successfully sent! Topic: {}, Partition: {}, Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                // Failure path (e.g., broker down, timeouts)
                log.error("❌ Failed to send message! Error: {}", ex.getMessage());
            }
        });
    }
}
