package org.example.streams;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class sum_average {


    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1,34,23,11);

        int[] a = {1,56,1,5,5,56};

       Double b =  Arrays.stream(a).average().getAsDouble();
        int sum = Arrays.stream(a).sum();
//        int sum = IntStream.range(0,2).map(i->a[i]).sum();//sum of first two

        int avg = (int)Arrays.stream(a).average().getAsDouble();
        int[] reverse = IntStream.rangeClosed(1,a.length).map(i->a[a.length-i]).toArray();
        System.out.println(Arrays.toString(reverse));

    }
}
