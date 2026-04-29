package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Touppercase {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("jkdf","Dkm");

        List<String> word = list.stream().map(String::toUpperCase).collect(Collectors.toList());

        System.out.println(word);
    }
}
