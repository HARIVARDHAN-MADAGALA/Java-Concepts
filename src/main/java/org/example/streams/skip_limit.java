package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class skip_limit {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1,2,3,4,5,6);

        List<Integer> skip = list.stream().skip(2).collect(Collectors.toList());        //[3, 4, 5, 6]
        List<Integer> limit = list.stream().limit(2).collect(Collectors.toList()); //[1, 2]

        System.out.println(skip);
        System.out.println(limit);


    }
}
