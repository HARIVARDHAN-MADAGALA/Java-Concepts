package org.example.streams;

import java.util.*;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class concstarray {

    public static void main(String[] args) {
        int[] a = {5,4,3,2,1};
        int[] b = {10,9,8,7,6};

        int[] c = IntStream.concat(Arrays.stream(a), Arrays.stream(b)).sorted().distinct().toArray();
        System.out.println(Arrays.toString(c));//[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

    }
}
