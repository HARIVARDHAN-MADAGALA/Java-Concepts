package org.example.streams;
import java.util.*;

public class lastelement {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(654,65468,651,884,56);
//        System.out.println(list.reversed());

 Integer last = list.stream().skip(list.size()-1).findFirst().get();//56

        System.out.println(last);
    }
}
