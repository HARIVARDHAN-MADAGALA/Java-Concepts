package org.example.Rough;

import org.example.genrics.Box;
import org.example.streams.INtstram;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Generate all contiguous substrings
//Input: "abc"

public class Rough {

    public static List<String> method(String word){

        char[] ch = word.toCharArray();

        List<String> list = new ArrayList<>();

        for(int i = 0; i < ch.length; i++){

            list.add(String.valueOf(ch[i]));
            StringBuilder curr = new StringBuilder(String.valueOf(ch[i]));
            for( int j = i + 1 ; j < ch.length; j++){
                curr = curr.append(ch[j]);
                list.add(String.valueOf(curr));
            }
        }
        return list;
    }



    public static void main(String[] args) throws InterruptedException {

       String word = "abcd";

        System.out.println(

                method(word)
        );
    }
}

