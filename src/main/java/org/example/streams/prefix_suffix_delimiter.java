package org.example.streams;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class prefix_suffix_delimiter {


    public static void main(String[] args) {



        List<String> fruits = List.of("apple", "banana", "mango");

       String lis = fruits.stream().collect(Collectors.joining("delimiter", "prefix", "suffix"));
        String joined = fruits.stream()
                .collect(Collectors.joining(" | ", "Fruits: [", "]"));
        System.out.println(joined);
    }
}
