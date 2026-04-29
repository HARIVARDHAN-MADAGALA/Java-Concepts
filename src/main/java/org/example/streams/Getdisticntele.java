package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Getdisticntele {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(43,43,23,33,23);

        List<Integer> unq = list.stream().distinct().collect(Collectors.toList());

        System.out.println(unq);
    }
}
