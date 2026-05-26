package org.example.JDBC.transactional_concepts;

import org.example.JDBC.jpa_performance.Department;
import org.example.JDBC.jpa_performance.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * =====================================================================
 * JPA & Spring Transactions: The Complete Masterclass
 * =====================================================================
 * This service contains comprehensive demonstrations, explanations, and
 * best practices for Spring's {@link Transactional} annotation.
 * 
 * It covers:
 * 1. Transaction Propagation (REQUIRED, REQUIRES_NEW, NESTED, etc.)
 * 2. Database Isolation Levels & Prevention of DB Anomalies
 * 3. Default & Configurable Rollback Rules (Checked vs Unchecked exceptions)
 * 4. The AOP Proxy Self-Invocation Gotcha (And how to solve it)
 * =====================================================================
 */
@Service
public class TransactionalConceptsDemoService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * ⚡ SELF-INJECTION FOR PROXY FIXED:
     * We autowire our own service bean lazily to bypass the Self-Invocation gotcha.
     * When we call self.innerRequiresNewMethod(), Spring correctly routes the call
     * through
     * the proxy, initiating the transaction behavior!
     */
    @Autowired
    @Lazy
    private TransactionalConceptsDemoService self;

    // =================================================================
    // CONCEPT 1: TRANSACTION PROPAGATION
    // =================================================================
    /**
     * Propagation defines what happens to the transaction boundary when one
     * transactional method calls another.
     * 
     * Types of Propagation: from the Child Method's perspective!
     * ---------------------
     * 1. REQUIRED (Default):
     *    - "Join my parent's transaction if they have one; create a new one for myself if they don't."
     *    - If an active parent transaction exists, join it. Else, create a new one.
     * 
     * 2. REQUIRES_NEW:
     *    - "I want my own transaction. Hey parent, I will suspend yours while I run my own separate transaction."
     *    - Suspends the parent transaction and always creates a new, independent child transaction.
     *    - Essential for writing audit logs, rate limiters, or notifications that must survive parent transaction rollbacks.
     * 
     * 3. NESTED:
     *    - "I want a nested transaction inside yours. If I fail, we only roll back to my savepoint."
     *    - Uses database Savepoints inside the parent transaction. Rolling back the child nested transaction rolls back to the
     *      savepoint, allowing the parent transaction to catch the error and continue.
     * 
     * 4. MANDATORY:
     *    - "Hey parent, you MUST call me inside an active transaction. If you don't, I will throw an exception and crash."
     *    - Throws an exception if called without an active transaction from the parent.
     * 
     * 5. SUPPORTS:
     *    - "If my parent has a transaction, I will run transactionally with them. If they don't, I'm happy to run non-transactionally."
     *    - Runs transactionally if a transaction exists, otherwise runs non-transactionally.
     * 
     * 6. NOT_SUPPORTED:
     *    - "I refuse to run inside a transaction. Even if my parent has a transaction, I will suspend it and run non-transactionally."
     *    - Always executes non-transactionally, suspending any active parent transaction.
     * 
     * 7. NEVER:
     *    - "I will throw an exception if my parent calls me inside an active transaction."
     *    - Throws an exception if called when a parent transaction is active.
     */

    /**
     * DEMO A: Propagation.REQUIRED
     * In this method, if we save a department and call auditService.logRequired(),
     * they run in the SAME transaction.
     * If an exception is thrown at the end of this method, BOTH the department and
     * the audit log are rolled back.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void demonstrateRequiredPropagation(String deptName) {
        System.out.println("🚀 [Demo REQUIRED] Creating department: " + deptName);
        Department dept = new Department(deptName);
        departmentRepository.save(dept);

        System.out.println("🚀 [Demo REQUIRED] Writing audit log using REQUIRED...");
        auditService.logRequired("Created Department: " + deptName);

        // Simulated crash
        System.out.println("❌ [Demo REQUIRED] Simulating runtime crash...");
        throw new RuntimeException("Simulated crash to test REQUIRED rollback.");
    }

    /**
     * DEMO B: Propagation.REQUIRES_NEW
     * In this method, auditService.logRequiresNew() runs in a NEW independent
     * transaction.
     * When that helper method completes, its transaction commits immediately.
     * If the outer method rolls back later, the Department insert is rolled back,
     * but the Audit Log remains persisted!
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void demonstrateRequiresNewPropagation(String deptName) {
        System.out.println("🚀 [Demo REQUIRES_NEW] Creating department: " + deptName);
        Department dept = new Department(deptName);
        departmentRepository.save(dept);

        System.out.println("🚀 [Demo REQUIRES_NEW] Writing audit log using REQUIRES_NEW...");
        auditService.logRequiresNew("Attempting to create Department: " + deptName);

        // Simulated crash
        System.out.println("❌ [Demo REQUIRES_NEW] Simulating runtime crash of main transaction...");
        throw new RuntimeException("Outer transaction rolled back, but log should survive!");
    }

    /**
     * DEMO C: Propagation.NESTED
     * In this method, auditService.logNested() runs in a nested transaction
     * (Savepoint).
     * We try to run the nested method. If it fails, we catch the exception.
     * The nested transaction rolls back to the savepoint, but the main transaction
     * can
     * still successfully commit!
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void demonstrateNestedPropagation(String deptName) {
        System.out.println("🚀 [Demo NESTED] Creating department: " + deptName);
        Department dept = new Department(deptName);
        departmentRepository.save(dept);

        try {
            System.out.println("🚀 [Demo NESTED] Calling nested service that will throw an exception...");
            // Inside logNested we simulate a save, but we throw a runtime exception here:
            auditService.logNested("Created Department: " + deptName);
            throw new RuntimeException("Simulated nested failure.");
        } catch (Exception ex) {
            System.out.println("✅ [Demo NESTED] Caught nested transaction exception! Outer transaction proceeds.");
            // Because we caught the exception, the main transaction commits the Department
            // but the nested Savepoint (Audit Log) is rolled back!
        }
    }

    // =================================================================
    // CONCEPT 2: TRANSACTION ISOLATION LEVELS
    // =================================================================
    /**
     * Isolation levels define how isolated a transaction is from modifications
     * made by concurrent transactions. It helps prevent three database anomalies:
     * 
     * 1. Dirty Read: Transaction A reads data modified by Transaction B before B
     * commits.
     * 2. Non-repeatable Read: Transaction A reads a row, Transaction B updates and
     * commits,
     * Transaction A reads the row again and finds different values.
     * 3. Phantom Read: Transaction A queries a range of rows, Transaction B inserts
     * and commits,
     * Transaction A runs the query again and sees new "phantom" rows.
     * 
     * Spring Isolation Levels:
     * -------------------------
     * - DEFAULT: Uses the underlying database's default isolation level
     * (PostgreSQL/Oracle/MySQL default is READ_COMMITTED).
     * 
     * - READ_UNCOMMITTED:
     * - Allows Dirty Reads, Non-repeatable Reads, and Phantom Reads.
     * - Highest concurrency, lowest safety.
     * 
     * - READ_COMMITTED:
     * - Prevents Dirty Reads. Allows Non-repeatable Reads and Phantom Reads.
     * - The most common and recommended choice for general applications.
     * 
     * - REPEATABLE_READ:
     * - Prevents Dirty Reads and Non-repeatable Reads. Allows Phantom Reads (though
     * PostgreSQL prevents them even at this level).
     * - Locks the read rows so they cannot be modified during the transaction.
     * 
     * - SERIALIZABLE:
     * - Prevents all anomalies.
     * - Slowest performance. Locks entire ranges of rows/tables, completely
     * serializing execution.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void demonstrateReadCommittedIsolation() {
        System.out.println("ℹ️ Running in READ_COMMITTED isolation level. Safe from Dirty Reads!");
        // First read
        long count1 = departmentRepository.count();
        System.out.println("   [First Read] Department count: " + count1);

        // Under read committed, if another thread inserts in between, count2 can change
        // (Phantom Read).
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void demonstrateRepeatableReadIsolation() {
        System.out.println("ℹ️ Running in REPEATABLE_READ isolation level. Safe from Dirty & Non-repeatable Reads!");
    }

    // =================================================================
    // CONCEPT 3: ROLLBACK RULES (Checked vs Unchecked Exceptions)
    // =================================================================
    /**
     * Spring's rollback mechanism is highly opinionated out-of-the-box:
     * 
     * ❌ Unchecked Exception (RuntimeException): Triggers rollback.
     * ❌ Errors (java.lang.Error): Triggers rollback.
     * 
     * ⚠️ Checked Exception (java.lang.Exception): DOES NOT TRIGGER ROLLBACK!
     * Spring assumes checked exceptions represent business recovery paths (e.g.
     * InsufficientFunds),
     * not fatal failures.
     */

    /**
     * Case A: Throws Unchecked (Runtime) Exception.
     * The department will NOT be saved. Transaction is rolled back.
     */
    @Transactional
    public void demonstrateDefaultRollbackUncheckedException(String name) {
        Department dept = new Department(name);
        departmentRepository.save(dept);
        System.out.println("⚠️ Saved department " + name + " in Transaction.");

        System.out.println("⚠️ Throwing CustomRuntimeException...");
        throw new CustomRuntimeException("Unchecked Exception thrown!");
    }

    /**
     * Case B: Throws Checked Exception.
     * The department WILL BE saved! The transaction commits successfully because
     * Spring ignores checked exceptions for rollbacks by default.
     */
    @Transactional
    public void demonstrateDefaultRollbackCheckedException(String name) throws CustomCheckedException {
        Department dept = new Department(name);
        departmentRepository.save(dept);
        System.out.println("⚠️ Saved department " + name + " in Transaction.");

        System.out.println("⚠️ Throwing CustomCheckedException...");
        throw new CustomCheckedException("Checked Exception thrown! (No rollback by default)");
    }

    /**
     * Case C: Configured Rollback for Checked Exception.
     * By adding rollbackFor = Exception.class, we instruct Spring to rollback on
     * ALL exceptions, including checked ones. The department will NOT be saved.
     */
    @Transactional(rollbackFor = Exception.class)
    public void demonstrateConfiguredRollbackCheckedException(String name) throws CustomCheckedException {
        Department dept = new Department(name);
        departmentRepository.save(dept);
        System.out.println("⚠️ Saved department " + name + " in Transaction.");

        System.out.println("⚠️ Throwing CustomCheckedException (configured to rollback)...");
        throw new CustomCheckedException("Checked Exception thrown! (Rollback will occur)");
    }

    // =================================================================
    // CONCEPT 4: THE SELF-INVOCATION GOTCHA (AOP Proxy Issue)
    // =================================================================
    /**
     * 🧠 SELF-INVOCATION EXPLAINED:
     * When client code calls a transactional service, it calls the Spring AOP
     * Proxy.
     * The proxy intercepts the call, opens a transaction, and delegates to the real
     * service bean.
     * 
     * However, if Method A calls Method B inside the SAME bean class:
     * - It uses direct method invocation (this.methodB()).
     * - The call bypasses the Spring AOP Proxy completely.
     * - Thus, any transaction annotation on Method B is completely IGNORED!
     */

    /**
     * Case A: Calling method locally bypassing proxy (This is a bug!).
     * Even though innerRequiresNewMethod() is configured with REQUIRES_NEW,
     * calling it directly via this.innerRequiresNewMethod() runs it in the parent
     * transaction. If the parent transaction crashes, BOTH are rolled back!
     */
    @Transactional
    public void outerMethodBypassingProxy(String name) {
        System.out.println("🚨 Entering outerMethodBypassingProxy");
        Department dept = new Department(name);
        departmentRepository.save(dept);

        try {
            // Direct call to local method: bypasses Spring proxy!
            this.innerRequiresNewMethod("Audit: direct local call for " + name);
        } catch (Exception e) {
            System.out.println("   Caught exception from local call.");
        }

        // Simulating crash of main transaction
        throw new RuntimeException("Crash main transaction!");
    }

    /**
     * Case B: Calling method using Self-Injection (Correct fix!).
     * By using the injected 'self' proxy object, the call passes through Spring's
     * proxy layer.
     * The REQUIRES_NEW transaction is correctly created and committed! The audit
     * log survives!
     */
    @Transactional
    public void outerMethodUsingSelfInjection(String name) {
        System.out.println("✅ Entering outerMethodUsingSelfInjection");
        Department dept = new Department(name);
        departmentRepository.save(dept);

        try {
            // Correct fix: call through the lazily self-injected proxy bean!
            self.innerRequiresNewMethod("Audit: self-injected call for " + name);
        } catch (Exception e) {
            System.out.println("   Caught exception from proxy call.");
        }

        // Simulating crash of main transaction
        throw new RuntimeException("Crash main transaction!");
    }

    /**
     * Secondary method to show propagation.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerRequiresNewMethod(String logMessage) {
        System.out.println("   [Proxy Target] Executing innerRequiresNewMethod...");
        auditLogRepository.save(new AuditLogEntity(logMessage));
    }
}
