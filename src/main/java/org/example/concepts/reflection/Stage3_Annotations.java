package org.example.concepts.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// Stage 3 — Custom Annotations + reading them at runtime via Reflection
/// This is EXACTLY how Spring reads @Autowired, @Service
/// and how JUnit finds and runs @Test methods

/// ══════════════════════════════════════════════════════════════
///  @Retention — controls HOW LONG the annotation lives
/// ══════════════════════════════════════════════════════════════
/// RetentionPolicy.SOURCE  → exists only in .java file, erased at compile time
///                           used by: @Override, @SuppressWarnings, Lombok
/// RetentionPolicy.CLASS   → saved in .class file but NOT loaded into JVM at runtime
///                           default if you don't specify — rarely used directly
/// RetentionPolicy.RUNTIME → loaded into JVM, readable via Reflection at runtime
///                           used by: Spring, JUnit, Hibernate, Jackson

/// ══════════════════════════════════════════════════════════════
///  @Target — controls WHERE the annotation can be applied
/// ══════════════════════════════════════════════════════════════
/// ElementType.TYPE        → class, interface, enum        (@Service, @Entity)
/// ElementType.FIELD       → instance/static fields        (@Autowired, @Column)
/// ElementType.METHOD      → methods                       (@Test, @Bean)
/// ElementType.PARAMETER   → method parameters             (@RequestParam)
/// ElementType.CONSTRUCTOR → constructors                  (@Autowired on constructor)

public class Stage3_Annotations {

    // ── 1. field-level annotation ──
    @Retention(RetentionPolicy.RUNTIME)   // RUNTIME — reflection can read it
    @Target(ElementType.FIELD)            // only on fields
    @interface Inject {
        String value() default "";
    }

    // ── 2. method-level annotation ──
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)           // only on methods
    @interface Test {
        String description() default "no description";
    }

    // ── 3. class-level annotation (like @Service, @Entity) ──
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)             // only on class / interface / enum
    @interface Component {
        String name() default "";
    }

    // ── 4. @Repeatable — apply same annotation multiple times on same target ──
    //    Step 1: container annotation that holds an array of @Role
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Roles {
        Role[] value();
    }
    //    Step 2: mark @Role as repeatable, pointing to its container @Roles
    @Repeatable(Roles.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Role {
        String value();
    }

    // ── a class that uses all 4 annotation types ──
    @Component(name = "orderService")         // class-level
    static class OrderService {

        @Inject("paymentGateway")             // field-level
        private String paymentService;

        @Inject("emailProvider")
        private String notificationService;

        @Test(description = "validates order creation flow")   // method-level
        public void createOrder() {
            System.out.println("createOrder() running...");
        }

        @Test(description = "validates order cancellation flow")
        public void cancelOrder() {
            System.out.println("cancelOrder() running...");
        }

        @Role("ADMIN")                        // repeatable — same annotation 3 times
        @Role("MANAGER")
        @Role("SUPPORT")
        public void deleteOrder() {
            System.out.println("deleteOrder() — only allowed roles can call this");
        }

        public void internalHelper() {
            System.out.println("this should NOT run — no @Test on it");
        }
    }

    // ── mini framework: auto-inject fields marked with @Inject ──
    static void autoInject(Object obj) throws Exception {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Inject annotation = field.getAnnotation(Inject.class);
                field.setAccessible(true);
                field.set(obj, "MockInstance_of_" + annotation.value());
                System.out.println("Injected -> " + field.getName() + " = " + field.get(obj));
            }
        }
    }

    // ── mini framework: run only methods marked with @Test ──
    static void runTests(Object obj) throws Exception {
        System.out.println("\n── Running @Test methods ──");
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                Test annotation = method.getAnnotation(Test.class);
                System.out.println("\nTest: " + method.getName()
                        + " | description: " + annotation.description());
                method.invoke(obj);
            }
        }
    }

    // ── read class-level @Component (like Spring bean registration) ──
    static void readComponentAnnotation(Class<?> clazz) {
        System.out.println("\n── @Component on class-level ──");
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            System.out.println("Bean registered with name: " + component.name());
        }
    }

    // ── read @Repeatable @Role annotations on methods ──
    static void readRepeatableRoles(Class<?> clazz) {
        System.out.println("\n── @Repeatable @Role annotations ──");
        for (Method method : clazz.getDeclaredMethods()) {
            Role[] roles = method.getAnnotationsByType(Role.class); // fetches all repeated values
            if (roles.length > 0) {
                System.out.print(method.getName() + "() allowed roles: ");
                for (Role role : roles) {
                    System.out.print("[" + role.value() + "] ");
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        OrderService service = new OrderService();

        System.out.println("── Auto Injection (like Spring @Autowired) ──");
        autoInject(service);

        runTests(service);

        readComponentAnnotation(OrderService.class);

        readRepeatableRoles(OrderService.class);
    }
}
