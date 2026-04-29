package org.example.streams.exception;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class exception {

    public static void main(String[] args) {


        List<Integer> list = Arrays.asList(12, 25, 40, 56, 85, 13,null);


        List<Integer> list2 = list.stream().filter(c-> {
                    try {

                        if (c == null)
                            throw new NullPointerException("value is null");


                        return c % 2 == 0;
                    } catch (Exception e) {
                        System.out.println("Exception caught while filtering: " + e.getMessage());
                        return false;
                    }
                }
        ).collect(Collectors.toList());

        System.out.println(list2);
    }



}


