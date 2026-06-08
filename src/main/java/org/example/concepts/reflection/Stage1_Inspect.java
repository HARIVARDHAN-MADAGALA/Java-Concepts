package org.example.concepts.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/// Stage 1 — Inspecting a class at runtime (read only)
/// Problem it solves: you can explore ANY class structure without knowing it at compile time

public class Stage1_Inspect {

    public static void main(String[] args) throws Exception {

        // ── 3 ways to get the Class object (entry point to Reflection) ──
        Class<?> clazz1 = Employee.class;                          // when you have the type
        Class<?> clazz2 = new Employee().getClass();               // when you have an instance
        Class<?> clazz3 = Class.forName("org.example.concepts.reflection.Employee"); // when you only have the name as String

        Class<?> clazz = clazz1;

        // ── basic class info ──
        System.out.println("── Class Info ──");
        System.out.println("Class name      : " + clazz.getName());
        System.out.println("Simple name     : " + clazz.getSimpleName());
        System.out.println("Superclass      : " + clazz.getSuperclass().getSimpleName());
        System.out.println("Is interface?   : " + clazz.isInterface());

        // ── inspect all fields ──
        System.out.println("\n── Fields (getDeclaredFields = all including private) ──");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(Modifier.toString(field.getModifiers())
                    + " " + field.getType().getSimpleName()
                    + " " + field.getName());
        }

        // ── inspect all methods ──
        System.out.println("\n── Methods (getDeclaredMethods = all including private) ──");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(Modifier.toString(method.getModifiers())
                    + " " + method.getReturnType().getSimpleName()
                    + " " + method.getName() + "()");
        }

        // ── inspect all constructors ──
        System.out.println("\n── Constructors ──");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.println(constructor.getName()
                    + " -> params: " + constructor.getParameterCount());
        }

        // ── create an instance dynamically using no-arg constructor ──
        System.out.println("\n── Create instance dynamically ──");
        Employee emp = (Employee) clazz.getDeclaredConstructor().newInstance();
        System.out.println(emp);
    }
}
