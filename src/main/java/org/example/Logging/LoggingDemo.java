package org.example.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A hands-on educational class to demonstrate everything about logging in Java.
 * Open this in your IDE and run the main method!
 */
public class LoggingDemo {

    // 1. Declare the SLF4J Logger.
    // We pass our class name to getLogger() so the logs will print exactly WHICH class generated the message.
    private static final Logger log = LoggerFactory.getLogger(LoggingDemo.class);

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("                  STARTING JAVA LOGGING DEMO                     ");
        System.out.println("=================================================================\n");

        System.out.println("--- STEP 1: Demonstrating Log Levels ---");
        demonstrateLogLevels();

        System.out.println("\n--- STEP 2: Demonstrating Parametric Logging (Placeholders) ---");
        demonstrateParametricLogging("admin_user", "192.168.1.100");

        System.out.println("\n--- STEP 3: Demonstrating Exception Logging (Stack Traces) ---");
        demonstrateExceptionLogging();

        System.out.println("\n=================================================================");
        System.out.println("Check your console above! If you don't see TRACE or DEBUG logs, ");
        System.out.println("it is because the active logging level is set to INFO by default.");
        System.out.println("Open 'src/main/resources/logback.xml' to change the active level!");
        System.out.println("=================================================================");
    }

    /**
     * Shows the hierarchy of levels.
     * TRACE (Lowest) -> DEBUG -> INFO -> WARN -> ERROR (Highest)
     */
    private static void demonstrateLogLevels() {
        // TRACE: Extremely noisy/detailed developer prints. Usually hidden in production.
        log.trace("TRACE: Entering demonstrateLogLevels() method execution loop.");

        // DEBUG: Details useful for debugging issues in a local/staging environment.
        log.debug("DEBUG: Initializing basic log level demo with static data.");

        // INFO: Key operational events showing the app is running healthy.
        log.info("INFO: The LoggingDemo has been successfully initialized and started.");

        // WARN: Something unexpected happened but the system can still run.
        log.warn("WARN: Low disk space detected (Simulated warning). Please monitor.");

        // ERROR: Something failed or crashed! High priority.
        log.error("ERROR: Failed to establish connection to a dummy database (Simulated error).");
    }

    /**
     * Demonstrates why placeholders '{}' are used instead of string concatenation "+".
     */
    private static void demonstrateParametricLogging(String username, String ipAddress) {
        // WRONG & SLOW WAY: String concatenation.
        // Even if debug logging is DISABLED, Java will still waste CPU/Memory combining these strings!
        // log.debug("User " + username + " attempted login from IP address " + ipAddress);

        // CORRECT & FAST WAY: Parametric placeholder.
        // If debug logging is disabled, SLF4J does zero string formatting, making it incredibly fast.
        log.info("User '{}' successfully logged in from IP address: {}", username, ipAddress);

        log.debug("Detailed login payload: username={}, source_ip={}, session_id={}", 
                username, ipAddress, "SES-491-XYZ");
    }

    /**
     * Demonstrates how to correctly log exceptions with their complete stack traces.
     */
    private static void demonstrateExceptionLogging() {
        try {
            // Let's force an exception to happen!
            int result = 10 / 0;
        } catch (ArithmeticException ex) {
            // WRONG WAY: Just printing the message. You lose the stack trace (where it happened)!
            // log.error("An error occurred: " + ex.getMessage());

            // WRONG WAY: log.error(ex.getMessage(), ex); (Redundant)

            // CORRECT WAY: Pass the message, and pass the exception object as the LAST argument.
            // SLF4J detects that the last argument is a Throwable, and automatically prints the complete stack trace!
            log.error("Failed to perform division calculation due to an arithmetic error", ex);
        }
    }
}
