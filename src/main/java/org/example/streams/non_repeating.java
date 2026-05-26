package org.example.streams;

import java.util.Collections;
import java.util.stream.Collectors;

//first non repeating char
public class non_repeating {
    public static void main(String[] args) {

        String name = "HSRIVSRFJSHN";
//** name.indexOf(c) returns the index of the first occurrence of c in the string. **//
        Character ch = name.chars()
                .mapToObj(c->(char)c)
                .filter(c->name.indexOf(c)==name.lastIndexOf(c))
                .findFirst()
                .orElse(null);



//        Character ch2 = name.chars()
//                .filter(c-> Collections.frequency(name.chars().boxed().collect(Collectors.toList()), c) == 1)
//                .mapToObj(c->(char)c)
//                .findFirst()
//                .orElse(null);

        System.out.println(ch);
    }
}
