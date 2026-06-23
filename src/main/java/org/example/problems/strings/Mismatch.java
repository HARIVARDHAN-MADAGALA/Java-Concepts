package org.example.problems.strings;

// 10. Write a method that should return difference of characters
// (missing char from given string) between given String and Input string.
// eg. Given String = "abcdefghijklmnopqrstuvwxyz";
//     Input String = "Online test with GS client";

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mismatch {

    public static List<Character> method(String word){

        String sentence = "abcdefghijklmnopqrstuvwxyz";

        List<Character> list = new ArrayList<>();

        word.replaceAll("\\s","");

        for(char i : sentence.toCharArray()){

            if(!word.contains(String.valueOf(i))){                 //        if(word.indexOf(ch) == -1){


                list.add(i);
            }

        }

        return list;

    }

    public static void main(String[] args) {


        String word = "Online test with GS client";
        System.out.println(

                method(word)
        );
    }
}

