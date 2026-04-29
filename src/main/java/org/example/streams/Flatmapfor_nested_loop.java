package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Flatmapfor_nested_loop {

    public static void main(String[] args) {

        List<List<Integer>> nest = Arrays.asList(Arrays.asList(1,2,3,4),Arrays.asList(5,6,7,8));

        List<Integer> flat = nest.stream().flatMap(x->x.stream()).collect(Collectors.toList());

        System.out.println(flat);

        String d = "sd";

        d.equals("sdf");

    }



}
