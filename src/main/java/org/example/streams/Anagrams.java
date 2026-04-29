package org.example.streams;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Anagrams {

    public static void main(String[] args) {

            String s1 = "listen";
            String s2 = "silent";

            /// Optimal

//        System.out.println(  Arrays.equals(
//
//                s1.toUpperCase().chars().sorted().toArray(),  s1.toUpperCase().chars().sorted().toArray()
//        ));

        //********************************************************************///
//        long a = IntStream.concat(s1.chars(),s2.chars()).distinct().count();
//        System.out.println(a);


        /// this too we can do

//                String s4 =   s1.chars().mapToObj(c -> String.valueOf((char)c).toUpperCase()).sorted().collect(Collectors.joining());


        s1 = Stream.of(s1.split("")).map(String::toUpperCase).sorted().collect(Collectors.joining());
        System.out.println(s1);

        s2 = Stream.of(s2.split("")).map(String::toUpperCase).sorted().collect(Collectors.joining());
        System.out.println(s2);



        System.out.println( (s1.equals(s2)) ? true : false);

    }
}

//String sorted1 = s1.toLowerCase()//string
//        .chars()//intstream
//        .sorted()
//        .mapToObj(c -> String.valueOf((char) c))
//        .collect(Collectors.joining());

