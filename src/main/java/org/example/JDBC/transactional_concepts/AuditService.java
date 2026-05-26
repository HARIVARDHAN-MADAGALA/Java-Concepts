package org.example.JDBC.transactional_concepts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper service to demonstrate Transaction Propagation behaviors across bean boundaries.
 * 
 * 🧠 WHY A SEPARATE SERVICE BEAN?
 * Spring @Transactional works using Spring AOP (Aspect-Oriented Programming) proxies.
 * When a bean is injected, Spring injects a dynamically generated proxy class that wraps
 * your bean and manages transaction start/commit/rollback around method calls.
 * 
 * If Method A inside `TransactionalDemoService` calls Method B inside the *same* class,
 * Java executes the call directly on the `this` reference, completely bypassing the Spring proxy.
 * As a result, the transaction configurations on Method B (like REQUIRES_NEW or NESTED) are IGNORED.
 * 
 * Therefore, to demonstrate propagation correctly, we must call methods across bean boundaries
 * (i.e. TransactionalDemoService calling AuditService).
 */
@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 1. Propagation.REQUIRED (Default)
     * Joins the active transaction if one exists. If none exists, creates a new one.
     * If the main transaction rolls back, this log will also roll back.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void logRequired(String action) {
        System.out.println("   [AuditService] Executing logRequired. Joining existing transaction if present.");
        auditLogRepository.save(new AuditLogEntity(action));
    }

    /**
     * 2. Propagation.REQUIRES_NEW
     * Always suspends the current transaction and starts a new, completely independent transaction.
     * The independent transaction commits immediately when this method finishes.
     * Even if the outer/main transaction rolls back later, this log WILL REMAIN persisted in the DB!
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequiresNew(String action) {
        System.out.println("   [AuditService] Executing logRequiresNew. Suspending outer transaction & starting a NEW transaction.");
        auditLogRepository.save(new AuditLogEntity(action));
    }

    /**
     * 3. Propagation.NESTED
     * Executes within a nested transaction if an active transaction exists.
     * It uses database "Savepoints" under the hood (supported by JDBC).
     * 
     * How it works:
     * - If the nested transaction fails (throws an exception), the changes are rolled back to the Savepoint.
     *   The outer transaction can catch the exception and CONTINUE/COMMIT its own changes.
     * - If the outer transaction rolls back, the nested transaction also rolls back.
     */
    @Transactional(propagation = Propagation.NESTED)
    public void logNested(String action) {
        System.out.println("   [AuditService] Executing logNested. Creating a nested transaction using Savepoint.");
        auditLogRepository.save(new AuditLogEntity(action));
    }
}
