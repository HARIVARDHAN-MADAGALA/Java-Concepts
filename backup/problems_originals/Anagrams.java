package org.example.problems.strings;

/// anagrams  =  same set of letters
import java.util.Arrays;

public class Anagrams {

    public static void main(String[] args) {

        String s1 = "silent";
        String s2 = "listen";

        char[] s3 = s1.toLowerCase().toCharArray();
        char[] s4 = s2.toLowerCase().toCharArray();

        Arrays.sort(s3);
        Arrays.sort(s4);

        System.out.println(Arrays.equals(s3,s4));
    }
}

/// Streams

//Boolean b = Arrays.equals(s1.chars().sorted().toArray(),  s2.chars().sorted().toArray());


