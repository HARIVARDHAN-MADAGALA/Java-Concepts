package org.example.streams;

import java.util.Arrays;
import java.util.Comparator;

public class highest_vowel {

    public static void main(String[] args) {

        String[] s1 = { "Harivardhan","Prashant","Griid Dynamics","TaTa" };

        System.out.println( Arrays.stream(s1).max(Comparator.comparingInt( highest_vowel::vowels)) );
    }

    static int vowels (String s){

        return (int) s.chars().filter( c -> "aeiou".indexOf( String.valueOf( (char)c)) != -1).count();

    }
}
