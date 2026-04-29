package org.example.streams;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class most_repeating_char {

    public static void main(String[] args) {

        List<String> words = List.of("java", "spring", "boot", "microservices");


        System.out.println(

                words.stream().flatMap(c -> c.chars().mapToObj(a->(char)a))
                        .collect(Collectors.groupingBy(c->c,Collectors.counting()))
                        .entrySet().stream().max(Comparator.comparingLong(c-> c.getValue()))
                        .map(c->c.getKey()).orElse(null)


        );




    }
}
