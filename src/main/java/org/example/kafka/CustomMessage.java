package org.example.kafka;

import java.time.LocalDateTime;

/**
 * A custom payload class representing a structured message to be sent/received via Kafka.
 * This class will be serialized into JSON by the Producer and deserialized back into a Java
 * object by the Consumer.
 */
public class CustomMessage {

    private String id;
    private String content;
    private String sentAt;

    public CustomMessage() {}

    public CustomMessage(String id, String content) {
        this.id = id;
        this.content = content;
        this.sentAt = LocalDateTime.now().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sentAt='" + sentAt + '\'' +
                '}';
    }
}
