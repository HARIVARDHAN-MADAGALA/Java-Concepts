package org.example.Conversions;

import java.util.Arrays;
import java.util.stream.Collectors;

public class split_String_to_StingArray {

    public static void main(String[] args) {

        String name = "harish";
        String[] s = name.split("");
        System.out.println(Arrays.toString(s));




        String word = "How are you doing";

        System.out.println(  Arrays.stream(word.split(" ")).map( c -> new StringBuilder(c).reverse().toString())
                .collect(Collectors.joining(" ")));

        String[] a = word.split(" ");

        int i =0;

        for( String wor : a){

            a[i] = new StringBuilder(wor).reverse().toString();
            i++;
        }



        System.out.println(Arrays.toString(a));
        System.out.println(String.join(" ", a));
    }
}
