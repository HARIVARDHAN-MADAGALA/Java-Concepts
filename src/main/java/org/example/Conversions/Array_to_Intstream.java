package org.example.Conversions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Array_to_Intstream {


    public static void main(String[] args) {

        int[] a = {1,2,3,4};

        IntStream b = Arrays.stream(a);


        ///  Again Intstream to array

        int[] c = b.toArray();


        /// Array to List

        String name = "HARI";
        List<String> list = Arrays.asList(name.split(""));
    }
}
