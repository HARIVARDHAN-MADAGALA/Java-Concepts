package org.example.streams;
import java.util.*;
import java.util.stream.Collectors;

public class duplicate {

    public static void main(String[] args) {

    List<Integer> list = Arrays.asList(234,345,345643,23,23,23443);


    HashSet<Integer> set = new HashSet<>();
    Set<Integer> list2 = list.stream().filter(i->!set.add(i)).collect(Collectors.toSet());
        System.out.println(list2);

    }
}
