package org.example.streams;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class filter {
    public static void main(String[] args) {


        List<String> emp = List.of("kjdf","lds","sdfds","kjdf");

        Set<String> result = emp.stream().collect(Collectors.toSet());

        System.out.println(result);
    }
}
