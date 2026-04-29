package org.example.streams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class first_nonduplicate {

        public static void main(String[] args) {

            List<Integer> l = Arrays.asList(1,5,3,4,2,1,2);

            System.out.println(l.stream().filter(c-> Collections.frequency(l,c) == 1).findFirst().get());
        }
    }

