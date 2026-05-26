package org.example.JDBC.transactional_concepts;

/**
 * A custom runtime (unchecked) exception to demonstrate default Spring transaction rollback behavior.
 * 
 * 🧠 ROLLBACK RULE:
 * By default, Spring's @Transactional will AUTOMATICALLY trigger a transaction rollback
 * when any Unchecked Exception (RuntimeException or its subclasses) is thrown from the transactional method.
 */
public class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String message) {
        super(message);
    }
}
