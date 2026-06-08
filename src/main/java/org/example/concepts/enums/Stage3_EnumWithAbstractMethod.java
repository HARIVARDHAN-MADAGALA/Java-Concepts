package org.example.concepts.enums;

/// Stage 3 — enum with abstract methods
/// Each constant has its OWN implementation of the method
/// Real world example: Payment modes — each calculates fee differently

public class Stage3_EnumWithAbstractMethod {

    enum PaymentMode {

        CREDIT_CARD {
            @Override
            public double calculateFee(double amount) {
                return amount * 0.02; // 2% fee
            }

            @Override
            public String label() {
                return "Credit Card";
            }
        },

        UPI {
            @Override
            public double calculateFee(double amount) {
                return 0; // no fee
            }

            @Override
            public String label() {
                return "UPI";
            }
        },

        NET_BANKING {
            @Override
            public double calculateFee(double amount) {
                return amount * 0.01; // 1% fee
            }

            @Override
            public String label() {
                return "Net Banking";
            }
        };

        // abstract — each constant MUST override this
        public abstract double calculateFee(double amount);
        public abstract String label();
    }

    static void processPayment(PaymentMode mode, double amount) {
        double fee   = mode.calculateFee(amount);
        double total = amount + fee;
        System.out.println(mode.label() + " | Amount: " + amount
                + " | Fee: " + fee + " | Total: " + total);
    }

    public static void main(String[] args) {

        processPayment(PaymentMode.CREDIT_CARD, 1000);
        processPayment(PaymentMode.UPI,         1000);
        processPayment(PaymentMode.NET_BANKING, 1000);
    }
}
