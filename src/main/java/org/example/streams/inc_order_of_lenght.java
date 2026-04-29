package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;

public class inc_order_of_lenght {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("hskf","dfsdf","dsb","sg");

        List<String> res = list.stream().sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
        System.out.println(res);//[sg, dsb, hskf, dfsdf]

    }
}
