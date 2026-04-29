package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class common_element {
    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1,34,23,11);
        List<Integer> list2 = Arrays.asList(1,5434,786,11);

        List<Integer> lsit3 = list.stream().filter(list2::contains).collect(Collectors.toList());
//        List<Integer> lsit3 = list.stream().filter(c->list2.contains(c)).collect(Collectors.toList());


        System.out.println(lsit3);

    }
}
