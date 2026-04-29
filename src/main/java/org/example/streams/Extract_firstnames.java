package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Extract_firstnames {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("Madagala Harivardhan","Manhu joker","Lamjas kohli");

        List<String> last = list.stream().map(x->x.split(" ")[0]).collect(Collectors.toList());

        System.out.println(last);
    }
}
