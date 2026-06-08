package org.example.concepts.classloader;

/// Stage 2 — Class Loading Lifecycle
/// A class goes through 5 phases before you can use it:
///
///  1. Loading    — ClassLoader reads .class bytecode into memory as a Class object
///  2. Linking
///     a. Verification  — bytecode is valid and safe (no stack overflows, type violations)
///     b. Preparation   — static fields allocated with DEFAULT values (0, null, false)
///     c. Resolution    — symbolic references (e.g. "java/lang/String") → direct memory refs
///  3. Initialization — static initializers + static field assignments run (in source order)
///
/// Key: static blocks run ONCE, lazily, when class is FIRST actively used
///      "active use" = new, static field access, static method call, Class.forName(), etc.

public class Stage2_ClassLoadingLifecycle {

    // ── demonstrates initialization order ──
    static class LazyInit {
        static int VALUE;

        static {
            System.out.println("  [static block] LazyInit initializing...");
            VALUE = 42;
        }

        static int get() { return VALUE; }
    }

    // ── Class.forName vs ClassLoader.loadClass ──
    // Class.forName(name)          → loads + initializes (static block runs)
    // loader.loadClass(name)       → loads ONLY, does NOT initialize

    public static void main(String[] args) throws Exception {

        System.out.println("── before touching LazyInit ──");

        // referencing the class triggers initialization
        System.out.println("── accessing LazyInit.VALUE ──");
        System.out.println("VALUE = " + LazyInit.VALUE); // static block runs here

        System.out.println("\n── second access — static block does NOT run again ──");
        System.out.println("VALUE = " + LazyInit.get());

        // ── forName initializes; loadClass does not ──
        System.out.println("\n── Class.forName (initializes) ──");
        Class<?> c1 = Class.forName(
            "org.example.concepts.classloader.Stage2_ClassLoadingLifecycle$LazyInit"
        );
        System.out.println("Loaded: " + c1.getSimpleName());

        System.out.println("\n── loadClass (no initialization) ──");
        ClassLoader loader = Stage2_ClassLoadingLifecycle.class.getClassLoader();
        Class<?> c2 = loader.loadClass(
            "org.example.concepts.classloader.Stage2_ClassLoadingLifecycle$LazyInit"
        );
        System.out.println("Loaded (no init): " + c2.getSimpleName());
        // static block won't print again — class was already initialized above
    }
}
