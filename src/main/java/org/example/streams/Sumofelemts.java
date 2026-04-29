package org.example.streams;

import java.util.Arrays;
import java.util.List;

public class Sumofelemts {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1,2,3,4);

        Integer sum = list.stream().mapToInt(Integer::intValue).sum();



        System.out.println(sum);
    }
}
