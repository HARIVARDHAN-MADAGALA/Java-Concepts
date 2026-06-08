package org.example.streams;

import java.util.*;

public class secound_largest {

    public static void main(String[] args) {

        List<Integer> num = Arrays.asList(23,233,2324,234,234,434);

        int res = num.stream().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();
        System.out.println(res);
    }
}
