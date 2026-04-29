package org.example.problems;

public class    printFibonoci {
    public static void printFibonacci(int n) {

        int a = 0, b = 1;

        if (n >= 1) System.out.print(a + " ");
        if (n >= 2) System.out.print(b + " ");

        for (int i = 3; i <= n; i++) {
            int c = a + b;
            System.out.print(c + " ");
            a = b;
            b = c;
        }


    }

    public static void main(String[] args) {

        printFibonacci(34);
    }
}
