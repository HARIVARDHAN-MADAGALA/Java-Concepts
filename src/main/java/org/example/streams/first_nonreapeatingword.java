package org.example.streams;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class first_nonreapeatingword {

    public static void main(String[] args) {


        List<String> names = List.of("Ram", "Shyam", "Sita", "Ravi", "Shyam");


        System.out.println(  names.stream().collect(Collectors.groupingBy(c ->c, LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream().filter(c -> c.getValue() ==1)
                .map(c-> c.getKey()).findFirst().get() );
    }
}
