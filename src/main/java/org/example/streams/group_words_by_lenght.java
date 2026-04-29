package org.example.streams;


import java.util.List;
import java.util.stream.Collectors;

public class group_words_by_lenght {

    public static void main(String[] args) {


        List<String> words = List.of("java", "spring", "boot", "api", "microservices","abc");

        System.out.println(


                words.stream().collect(Collectors.groupingBy(c->c.length()))
        );


    }
}
