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

// Sort words in a string
//Input: "Hello World Java"
//Output: "Java World Hello"

public class Rough {

    public static String method(String word){

        return  String.join( " ",Arrays.stream(word.split(" ")).sorted(Comparator.reverseOrder()).toList() );

    }



    public static void main(String[] args) throws InterruptedException {

         A a = A.getInstance();
         A b = A.getInstance();

        System.out.println(
                 a == b
        );


    }
}

