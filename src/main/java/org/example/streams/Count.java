package org.example.streams;

import java.util.Arrays;
import java.util.List;

//Problem: Count the number of elements in a list that are greater than 55.
public class Count {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(223,43,23);

        Long count = list.stream().filter(x -> x>55).count();

        System.out.println(count);

    }
}
