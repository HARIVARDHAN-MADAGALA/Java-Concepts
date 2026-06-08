package org.example.concepts.classloader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/// Stage 3 — Custom ClassLoader
/// Why write one?
///  - Load classes from a non-standard source (network, DB, encrypted jar, ZIP)
///  - Hot-reload: load a fresh version of a class at runtime (used by Spring DevTools, JRebel)
///  - Class isolation: same class loaded by two different loaders = two distinct types (OSGi, plugins)
///
/// To write one:
///  - extend ClassLoader
///  - override findClass(String name)  ← preferred hook (parent delegation still works)
///  - call defineClass(name, bytes, offset, length) to hand bytecode to JVM

public class Stage3_CustomClassLoader {

    static class ByteArrayClassLoader extends ClassLoader {

        private final byte[] bytecode;
        private final String className;

        ByteArrayClassLoader(String className, byte[] bytecode) {
            super(Stage3_CustomClassLoader.class.getClassLoader()); // set parent
            this.className = className;
            this.bytecode  = bytecode;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(className)) {
                System.out.println("  [ByteArrayClassLoader] defining class: " + name);
                return defineClass(name, bytecode, 0, bytecode.length); // hand to JVM
            }
            throw new ClassNotFoundException(name);
        }
    }

    // ── reads .class bytecode from the classpath as raw bytes ──
    static byte[] readClassBytes(String className) throws IOException {
        String path = className.replace('.', '/') + ".class";
        try (InputStream in = ClassLoader.getSystemResourceAsStream(path)) {
            if (in == null) throw new IOException("Class resource not found: " + path);
            return new DataInputStream(in).readAllBytes();
        }
    }

    public static void main(String[] args) throws Exception {

        String targetClass = "org.example.concepts.classloader.Stage3_CustomClassLoader$Greeter";

        // ── load Greeter's bytecode as raw bytes ──
        byte[] bytes = readClassBytes(targetClass);
        System.out.println("Bytecode size: " + bytes.length + " bytes");

        // ── load the class through our custom loader ──
        ByteArrayClassLoader customLoader = new ByteArrayClassLoader(targetClass, bytes);
        Class<?> greeterClass = customLoader.loadClass(targetClass);

        System.out.println("Loaded by : " + greeterClass.getClassLoader().getClass().getSimpleName());

        // ── invoke via reflection (can't cast — different class identity) ──
        Object instance = greeterClass.getDeclaredConstructor().newInstance();
        greeterClass.getMethod("greet").invoke(instance);

        // ── class identity: same bytecode, different loader = different type ──
        System.out.println("\nGreeter == Greeter (same loader)? " +
            (Greeter.class == greeterClass)); // false — different class objects
    }

    // ── simple class to load dynamically ──
    public static class Greeter {
        public void greet() {
            System.out.println("Hello from dynamically loaded Greeter!");
        }
    }
}
