package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;

public class palindrome {

    public static void main(String[] args) {


        List<String> words = Arrays.asList("madam", "hello", "racecar", "java", "level", "world");

        List<String> palindromes = words.stream()
                .filter(c -> c.equals(new StringBuilder(c).reverse().toString())).collect(Collectors.toList());

        System.out.println(palindromes);
    }

}
