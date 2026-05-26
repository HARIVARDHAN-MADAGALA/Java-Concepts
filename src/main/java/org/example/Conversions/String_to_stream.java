package org.example.Conversions;

import java.util.Map;
import java.util.stream.Collectors;

public class String_to_stream {

    public static void main(String[] args) {
        String name = "harivardhan";

        name.chars();// instream

        name.chars().mapToObj(c -> (char) c);// Stream of Character objects

        Map<Character, Long> s1 = name.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        System.out.println(s1);

    }
}
