package org.example.concepts.classloader;

/// Stage 1 — What is a ClassLoader?
/// JVM does NOT load all classes at startup — it loads them lazily, on first use
/// ClassLoader is responsible for: finding .class bytecode → loading it → linking it → initializing it
///
/// ClassLoader Hierarchy (Parent Delegation Model):
///
///   Bootstrap ClassLoader       ← loads rt.jar / java.* (written in native C, not Java)
///         ↑ parent
///   Platform ClassLoader        ← loads javax.*, java extensions (was ExtClassLoader in Java 8)
///         ↑ parent
///   Application ClassLoader     ← loads YOUR classes from classpath
///         ↑ parent
///   Custom ClassLoader          ← you can write your own (plugins, hot-reload, isolation)
///
/// Parent Delegation: before loading a class, a ClassLoader ALWAYS asks its parent first
/// This prevents malicious classes from shadowing java.lang.String etc.

public class Stage1_WhatIsClassLoader {

    public static void main(String[] args) {

        // ── every class knows its own ClassLoader ──
        ClassLoader appLoader      = Stage1_WhatIsClassLoader.class.getClassLoader();
        ClassLoader platformLoader = appLoader.getParent();
        ClassLoader bootstrapLoader = platformLoader.getParent(); // null — it's native

        System.out.println("App ClassLoader      : " + appLoader);
        System.out.println("Platform ClassLoader : " + platformLoader);
        System.out.println("Bootstrap ClassLoader: " + bootstrapLoader); // prints null

        // ── bootstrap loads java.lang.* — no Java object represents it ──
        System.out.println("\nString's ClassLoader : " + String.class.getClassLoader()); // null
        System.out.println("Thread's ClassLoader : " + Thread.class.getClassLoader()); // null

        // ── your class is loaded by the Application ClassLoader ──
        System.out.println("\nThis class loaded by : " + Stage1_WhatIsClassLoader.class.getClassLoader().getClass().getSimpleName());

        // ── Class.forName() triggers loading + initialization ──
        try {
            Class<?> clazz = Class.forName("java.util.ArrayList");
            System.out.println("\nLoaded via forName   : " + clazz.getName());
            System.out.println("Its ClassLoader      : " + clazz.getClassLoader()); // null → bootstrap
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
