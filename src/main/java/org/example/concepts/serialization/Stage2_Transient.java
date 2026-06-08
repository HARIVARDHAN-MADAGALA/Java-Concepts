package org.example.concepts.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/// Stage 2 — transient keyword
/// Problem it solves: skip sensitive fields (password, salary, card number)
/// so they are NOT written to disk or sent over network

public class Stage2_Transient {

    static class User implements Serializable {

        String username;
        transient String password;   // transient → skipped during serialization
        transient double salary;     // transient → skipped during serialization
        String role;

        User(String username, String password, double salary, String role) {
            this.username = username;
            this.password = password;
            this.salary   = salary;
            this.role     = role;
        }

        @Override
        public String toString() {
            return "User{username=" + username
                    + ", password=" + password    // will be null after deserialize
                    + ", salary=" + salary        // will be 0.0 after deserialize
                    + ", role=" + role + "}";
        }
    }

    static final String FILE = "user.ser";

    public static void main(String[] args) throws Exception {

        User original = new User("hari", "secret123", 75000.0, "ADMIN");
        System.out.println("Before serialization : " + original);

        // serialize
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(original);
        }

        // deserialize
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            User restored = (User) ois.readObject();
            System.out.println("After  deserialization: " + restored);
            // password → null  (transient String default = null)
            // salary   → 0.0   (transient double default = 0.0)
        }
    }
}
