package org.example.streams;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class frequency {
    public static void main(String[] args) {
        String name = "harivardhan";
      List<Integer> list =  List.of(2,2,21,1,3,4,32,2,1,2,4);

        Map<Character,Long> result = name.chars().mapToObj(c->(char)c)
                .collect(Collectors.groupingBy(c->c,Collectors.counting()));//{a=3, r=2, d=1, v=1, h=2, i=1, n=1}

        Set<Integer> dup = list.stream()
                        .filter(c->Collections.frequency(list,c)>1)
                                .collect(Collectors.toSet());
        System.out.println(dup);


        System.out.println(result);
    }


}
