package org.example.concepts.classloader;

import java.io.IOException;
import java.io.InputStream;

/// Stage 4 — Class Isolation + "Hot Reload" simulation
/// Real-world use: app servers (Tomcat), plugin systems (IntelliJ), Spring DevTools
///
/// Key insight: two ClassLoader instances loading the SAME .class file produce
///              two DISTINCT Class objects that are type-incompatible with each other
///              → JVM treats them as completely different types
///
/// This is how Tomcat isolates webapps — each webapp gets its own ClassLoader
/// Hot-reload = throw away old ClassLoader, create new one, reload class from disk

public class Stage4_HotReloadSimulation {

    // ── an isolated loader — does NOT delegate to parent for target class ──
    static class IsolatedLoader extends ClassLoader {

        private final String isolatedPrefix;

        IsolatedLoader(String isolatedPrefix) {
            super(null); // no parent delegation — fully isolated
            this.isolatedPrefix = isolatedPrefix;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.startsWith(isolatedPrefix)) {
                byte[] bytes = loadBytes(name);
                return defineClass(name, bytes, 0, bytes.length);
            }
            // fall back to bootstrap for java.* etc.
            return Class.forName(name, false, ClassLoader.getPlatformClassLoader());
        }

        private byte[] loadBytes(String className) throws ClassNotFoundException {
            String path = className.replace('.', '/') + ".class";
            try (InputStream in = ClassLoader.getSystemResourceAsStream(path)) {
                if (in == null) throw new ClassNotFoundException(className);
                return in.readAllBytes();
            } catch (IOException e) {
                throw new ClassNotFoundException(className, e);
            }
        }
    }

    interface Plugin {
        String execute();
    }

    // ── simulates a reloadable plugin ──
    public static class MyPlugin implements Plugin {
        @Override
        public String execute() { return "Plugin v1 running"; }
    }

    public static void main(String[] args) throws Exception {

        String pluginClass = "org.example.concepts.classloader.Stage4_HotReloadSimulation$MyPlugin";

        // ── load v1 ──
        IsolatedLoader loader1 = new IsolatedLoader("org.example.concepts.classloader");
        Class<?> v1 = loader1.loadClass(pluginClass);
        // Note: can't cast to Plugin — loader1 has no parent, so Plugin interface
        // was also loaded in isolation; use reflection to call execute()
        Object instance1 = v1.getDeclaredConstructor().newInstance();
        String result1   = (String) v1.getMethod("execute").invoke(instance1);
        System.out.println("v1 result : " + result1);
        System.out.println("v1 loaded by: " + v1.getClassLoader());

        // ── simulate "hot reload" — create a fresh loader ──
        IsolatedLoader loader2 = new IsolatedLoader("org.example.concepts.classloader");
        Class<?> v2 = loader2.loadClass(pluginClass);
        Object instance2 = v2.getDeclaredConstructor().newInstance();
        String result2   = (String) v2.getMethod("execute").invoke(instance2);
        System.out.println("\nv2 result : " + result2);
        System.out.println("v2 loaded by: " + v2.getClassLoader());

        // ── same bytecode, different loaders → different Class objects ──
        System.out.println("\nv1 == v2 ? " + (v1 == v2)); // false
        System.out.println("v1.equals(v2) ? " + v1.equals(v2)); // false

        // ── old loader can be GC'd → classes it loaded become eligible for unloading ──
        // (this is how Tomcat undeploys webapps without restarting the JVM)
        loader1 = null;
        System.gc();
        System.out.println("\nloader1 released — its classes are now eligible for unloading");
    }
}
