package org.example.concepts.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/// Stage 3 — serialVersionUID
/// Problem it solves: version mismatch between saved bytes and current class definition
///
/// What happens WITHOUT serialVersionUID:
///   1. You serialize an Employee object → saved to file
///   2. You add a new field to Employee class
///   3. JVM auto-generates a NEW serialVersionUID for the changed class
///   4. Old file has old UID, class has new UID → InvalidClassException 💥
///
/// Fix: declare serialVersionUID explicitly so YOU control compatibility

public class Stage3_SerialVersionUID {

    // ── Version 1 of the class ──
    // Imagine you serialized objects with this class and saved to disk
    static class ProductV1 implements Serializable {

        private static final long serialVersionUID = 1L; // explicitly declared

        String name;
        double price;

        ProductV1(String name, double price) {
            this.name  = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Product{name=" + name + ", price=" + price + "}";
        }
    }

    // ── Version 2 of the class — new field 'category' added ──
    // Because serialVersionUID is still 1L, old serialized bytes are still compatible
    // The new field 'category' will just be null for old objects
    static class ProductV2 implements Serializable {

        private static final long serialVersionUID = 1L; // same UID = backward compatible

        String name;
        double price;
        String category; // newly added field — old serialized data won't have this

        ProductV2(String name, double price, String category) {
            this.name     = name;
            this.price    = price;
            this.category = category;
        }

        @Override
        public String toString() {
            return "Product{name=" + name + ", price=" + price
                    + ", category=" + category + "}";  // category = null for old data
        }
    }

    static final String FILE = "product.ser";

    public static void main(String[] args) throws Exception {

        // serialize using V1
        ProductV1 v1 = new ProductV1("Laptop", 80000.0);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(v1);
            System.out.println("Serialized V1: " + v1);
        }

        // deserialize as V2 — works because serialVersionUID matches
        // category will be null since old data didn't have it
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            ProductV2 v2 = (ProductV2) ois.readObject();
            System.out.println("Deserialized as V2: " + v2);
            // category = null — safe, no exception
        }

        // ── key takeaway ──
        // If serialVersionUID was NOT declared and you added the 'category' field,
        // JVM would auto-generate different UIDs for V1 and V2
        // → InvalidClassException at runtime when trying to deserialize old data
    }
}
