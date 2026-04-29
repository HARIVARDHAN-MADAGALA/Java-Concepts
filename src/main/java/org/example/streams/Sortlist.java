package org.example.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sortlist {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(223,43,23);

        List<Integer> sort = list.stream().sorted().collect(Collectors.toList());
// In resverse order
        System.out.println(list.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));


        System.out.println(sort);

    }
}
