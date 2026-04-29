package org.example.streams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Filter_even_num {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<Integer> even = numbers.stream()
                .filter(n -> (n%2==0))
                .collect(Collectors.toList());

        System.out.println(even);

      

    }
}
