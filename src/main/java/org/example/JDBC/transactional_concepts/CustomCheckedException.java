package org.example.JDBC.transactional_concepts;

/**
 * A custom checked exception to demonstrate default Spring transaction rollback behavior.
 * 
 * 🧠 ROLLBACK RULE:
 * By default, Spring's @Transactional will NOT trigger a transaction rollback when a Checked Exception
 * (Exception class and subclasses other than RuntimeException) is thrown.
 * 
 * Instead, Spring assumes checked exceptions are "expected business results" that can be handled 
 * programmatically, and still commits the transaction!
 * 
 * If you want to rollback on a checked exception, you must explicitly configure it like so:
 * @Transactional(rollbackFor = CustomCheckedException.class)
 */
public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}
