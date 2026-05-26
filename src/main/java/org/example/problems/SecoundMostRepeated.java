package org.example.problems;

import org.example.Rough.B;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/// 28. Find the 2nd most repeated character in a string.
/// For example:
/// input="aaaabbccbdd"
/// outut="b"
///

public class SecoundMostRepeated {

    public static void main(String[] args) {

        String word = "aaaabbccbdd";
//        B obj1 = new B();

        System.out.println(
               word.chars().mapToObj(c -> (char)c).collect(Collectors.groupingBy(c->c, Collectors.counting()))
                        .entrySet().stream().sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                       .skip(1).findFirst().get().getKey()

        );

    }
}
