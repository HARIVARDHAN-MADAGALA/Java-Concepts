package org.example.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortbyMarksOfEmplyoees {

    public static void main(String[] args) {
    Employees e1 = new Employees(1,99,"hari");
    Employees e2 = new Employees(2,93,"hari1");

    Employees e3 = new Employees(2,39,"hari2");

    Employees e4 = new Employees(3,349,"hari3");

    ArrayList<Employees> list = new ArrayList<>();
    list.add(e1);
    list.add(e2);
    list.add(e3);
    list.add(e4);

         list.stream().sorted(Comparator.comparing(c->c.marks))
            .forEach(System.out::println);


    }


}
