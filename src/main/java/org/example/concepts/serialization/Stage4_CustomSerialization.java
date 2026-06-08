package org.example.concepts.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/// Stage 4 — Custom Serialization
/// Override writeObject / readObject to control exactly what gets serialized
/// Bonus: Serialization as a deep copy trick

public class Stage4_CustomSerialization {

    static class BankAccount implements Serializable {

        private static final long serialVersionUID = 1L;

        String accountHolder;
        double balance;
        transient String rawPassword;       // won't serialize as-is

        BankAccount(String accountHolder, double balance, String rawPassword) {
            this.accountHolder = accountHolder;
            this.balance       = balance;
            this.rawPassword   = rawPassword;
        }

        // ── custom write: encrypt password before saving ──
        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();                          // serialize non-transient fields normally
            String encrypted = "ENC:" + rawPassword + ":END"; // simulate encryption
            oos.writeObject(encrypted);                        // manually write encrypted value
            System.out.println("Custom write: password encrypted as → " + encrypted);
        }

        // ── custom read: decrypt password after loading ──
        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();                           // deserialize non-transient fields normally
            String encrypted = (String) ois.readObject();     // read the manually written value
            this.rawPassword = encrypted.replace("ENC:", "").replace(":END", ""); // simulate decrypt
            System.out.println("Custom read: password decrypted as → " + rawPassword);
        }

        @Override
        public String toString() {
            return "BankAccount{holder=" + accountHolder
                    + ", balance=" + balance
                    + ", password=" + rawPassword + "}";
        }
    }

    static final String FILE = "bankaccount.ser";

    // ── Bonus: deep copy using serialization ──
    // serialize to in-memory byte array → deserialize back = perfect deep copy
    @SuppressWarnings("unchecked")
    static <T extends Serializable> T deepCopy(T obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {

        BankAccount original = new BankAccount("Hari", 50000.0, "secret123");
        System.out.println("Original: " + original);

        // ── custom serialization ──
        System.out.println("\n── Custom Serialization ──");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(original);
        }

        System.out.println("\n── Custom Deserialization ──");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            BankAccount restored = (BankAccount) ois.readObject();
            System.out.println("Restored: " + restored);
        }

        // ── deep copy trick ──
        System.out.println("\n── Deep Copy via Serialization ──");
        BankAccount copy = deepCopy(original);
        copy.accountHolder = "Kumar";
        copy.balance       = 99999.0;

        System.out.println("Original after copy modified: " + original); // unchanged
        System.out.println("Copy: " + copy);
    }
}
