package org.example.streams;

import java.util.*;

public class Findmax {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(12,34,23,11);



       Integer num = list.stream().max((a, b) -> Integer.compare(a,b)).get();
       Integer num2 = list.stream().min(Comparator.naturalOrder()).get();

//        Integer num = list.stream().max(Integer::compare).get();
//        Integer num2 = list.stream().min(Comparator.naturalOrder()).get();


//    Integer num = list.stream().sorted(Collections.reverseOrder()).findFirst().get();
      //  Integer num = list.stream().max((a, b) -> (a<b ? -1 : ((a == b) ? 0:1))).get();

        System.out.println(num);
    }
}
