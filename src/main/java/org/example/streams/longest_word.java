package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;

public class longest_word {

    public static void main(String[] args) {

        List<String> s = Arrays.asList("abc","asdv","d");
        HashMap<String,Integer> m = new HashMap<>();

        System.out.println(s.stream().sorted((a,b)-> Integer.compare(b.length(), a.length()))
                .findFirst().get());
//
//        2nd way
//        String longest = words.stream()
//                .reduce((a, b) -> a.length() >= b.length() ? a : b)
//                .orElse(null);
//
//         /3rd way
//        String longest = words.stream()
//                .max(Comparator.comparingInt(String::length))
//                .orElse(null);

//        4th way
//        s.stream().min((a, b) -> Integer.compare(b.length(), a.length())).get()

    }
}
