package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;

public class ALL_sorting_Findmax {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(12,34,23,11);



       Integer num = list.stream().max((a, b) -> Integer.compare(a,b)).get();
       Integer num2 = list.stream().min(Comparator.naturalOrder()).get();
       Integer max = list.stream().collect(Collectors.maxBy((a, b) -> a.compareTo(b))).get();
        Integer numk = list.stream().max(Comparator.comparing((a -> a))).get();
//       Integer num = list.stream().max(Integer::compare).get();
//       Integer num2 = list.stream().min(Comparator.naturalOrder()).get();
//        Integer num = list.stream().sorted(Collections.reverseOrder()).findFirst().get();
      //  Integer num = list.stream().max((a, b) -> (a<b ? -1 : ((a == b) ? 0:1))).get();

        System.out.println(num);
    }
}
