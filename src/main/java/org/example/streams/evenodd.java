package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;


public class evenodd {

    public static void main(String[] args) {


        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        Map<Boolean, List<Integer>> res = list.stream().collect(Collectors.partitioningBy(i -> i % 2 == 0));
        System.out.println(res);


        ///  Sum of even numbers
        System.out.println(list.stream().filter(c -> c%2 == 0).mapToInt(Integer::intValue).sum());


    }
}
