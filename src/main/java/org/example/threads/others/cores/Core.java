package org.example.threads.others.cores;

public class Core {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + cores);
    }
}
