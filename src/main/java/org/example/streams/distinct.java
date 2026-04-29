package org.example.streams;
import java.util.*;
import java.util.stream.Collectors;

public class distinct {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(7,9,5,89,89,8974,74,5,6,5);

        System.out.println(list.stream().distinct().collect(Collectors.toList()));
    }
}
