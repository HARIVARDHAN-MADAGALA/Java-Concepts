package org.example.concepts.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/// Stage 1 — Basic Serialization
/// Problem it solves: save a Java object to disk and restore it later

public class Stage1_BasicSerialization {

    // must implement Serializable — it's a marker interface (no methods)
    // without this → NotSerializableException at runtime
    static class Employee implements Serializable {

        int    id;
        String name;
        double salary;

        Employee(int id, String name, double salary) {
            this.id     = id;
            this.name   = name;
            this.salary = salary;
        }

        @Override
        public String toString() {
            return "Employee{id=" + id + ", name=" + name + ", salary=" + salary + "}";
        }
    }

    static final String FILE = "employee.ser";

    // Object → bytes → file
    static void serialize(Employee emp) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(emp);
            System.out.println("Serialized: " + emp);
        }
    }

    // file → bytes → Object
    static Employee deserialize() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (Employee) ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {

        Employee original = new Employee(1, "Hari", 75000.0);

        serialize(original);

        Employee restored = deserialize();
        System.out.println("Deserialized: " + restored);

        // prove they are different objects (different memory address)
        System.out.println("Same object? " + (original == restored)); // false
    }
}
