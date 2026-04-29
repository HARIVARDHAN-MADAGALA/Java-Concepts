package org.example.problems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DifferentSubarrays {

    public static List<String> method (String word){

        List<String> result = new ArrayList<>();

        for(int i = 0 ; i < word.length(); i++){
            for(int j = i + 1; j <= word.length(); j++){
                result.add(word.substring(i,j));
            }
        }

        return result;
    }

    public static void main(String[] args) {

        String word = "abcd";

        System.out.println(

                method(word)
        );
    }

    ExecutorService executor = Executors.newFixedThreadPool(3);

}
