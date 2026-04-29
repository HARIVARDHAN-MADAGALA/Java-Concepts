package org.example.Conversions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Stream_to_Intstream {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(12,25,40,56,85,13);

        IntStream s = list.stream().mapToInt(Integer::intValue);
        DoubleStream s2 = list.stream().mapToDouble(Double::valueOf);
        LongStream s3 = list.stream().mapToLong(Long::valueOf);

        s2.forEach(System.out::println);

    }
}
