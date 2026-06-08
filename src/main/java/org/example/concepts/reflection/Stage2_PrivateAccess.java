package org.example.concepts.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// Stage 2 — Accessing private fields and invoking private methods
/// Problem it solves: frameworks and testing tools need to access internals
/// without the class exposing them publicly

public class Stage2_PrivateAccess {

    public static void main(String[] args) throws Exception {

        Employee emp = new Employee(1, "Hari", 75000.0, "Engineering");
        System.out.println("Before: " + emp);

        // ── read a private field ──
        System.out.println("\n── Read private field 'salary' ──");
        Field salaryField = emp.getClass().getDeclaredField("salary");
        salaryField.setAccessible(true);                          // unlock private access
        double salary = (double) salaryField.get(emp);
        System.out.println("Salary (via reflection): " + salary);

        // ── modify a private field ──
        System.out.println("\n── Modify private field 'salary' ──");
        salaryField.set(emp, 90000.0);                            // set new value
        System.out.println("After salary update: " + emp);

        // ── invoke a private method ──
        System.out.println("\n── Invoke private method 'applyBonus(double)' ──");
        Method bonusMethod = emp.getClass()
                .getDeclaredMethod("applyBonus", double.class);   // method name + param types
        bonusMethod.setAccessible(true);                          // unlock private access
        bonusMethod.invoke(emp, 10.0);                            // invoke with 10% bonus

        System.out.println("After bonus: " + emp);

        // ── read all fields and their values dynamically ──
        System.out.println("\n── All fields and values of the object ──");
        for (Field field : emp.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            System.out.println(field.getName() + " = " + field.get(emp));
        }
    }
}
