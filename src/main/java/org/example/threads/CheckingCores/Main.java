package org.example.threads.CheckingCores;

public class Main {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU Cores: " + cores);
    }
}