package org.example.Conversions;

public class Remove_NonAlphabets {

    public static String removeNonAlphabets(String senctence) {

        String s = senctence.replaceAll("[^a-zA-Z]", "");

        return s;

    }

    public static void main(String[] args) {

        String s1 = "Hello12345World!@#$%^&*()_+=-[]{}|;':./<>?";

        String s2 = removeNonAlphabets(s1);

        System.out.println(s2);

    }

}
