package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class reverse_each_word {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("Madagala Harivardhan","Manhu joker","Lamjas kohli");

        List<String> list2 = list.parallelStream().map(c->new StringBuilder(c).reverse().toString()).collect(Collectors.toList());
        System.out.println(list2);

    }
}
