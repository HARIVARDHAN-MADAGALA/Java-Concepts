package org.example.JDBC.transactional_concepts;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A simple JPA Entity to represent database audit logs.
 * Used to demonstrate propagation behavior like REQUIRES_NEW.
 * 
 * 🧠 THE USE CASE:
 * If a main transaction fails and rolls back, we still want our audit log
 * to be saved to the database. By using propagation = REQUIRES_NEW on the
 * logging method, the audit log runs in a separate independent transaction
 * and will persist even if the parent transaction fails and rolls back!
 */
@Entity
@Table(name = "transaction_audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(name = "audit_seq", sequenceName = "transaction_audit_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AuditLogEntity() {}

    public AuditLogEntity(String action) {
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
