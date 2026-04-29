package org.example.Conversions;

import java.util.Arrays;

public class string_into_a_character_array {

    public static void main(String[] args) {

        String name = "harish";

        char[] chararray = name.toCharArray();

        System.out.println(Arrays.toString(chararray));
        chararray = new StringBuilder(chararray.toString()).reverse().toString().toCharArray();
        System.out.println(Arrays.toString(chararray));

    }
}
