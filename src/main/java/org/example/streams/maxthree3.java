package org.example.streams;
import java.util.*;
import java.util.stream.Collectors;

public class maxthree3 {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(534,6545,51,54,3,3,68,31,65);

        List<Integer> res = list.stream().sorted().limit(3).toList(); //smallet 3
        List<Integer> res1 = list.stream().sorted(Comparator.reverseOrder()).limit(3).collect(Collectors.toList());//max 3


        System.out.println(res1);
    }
}
