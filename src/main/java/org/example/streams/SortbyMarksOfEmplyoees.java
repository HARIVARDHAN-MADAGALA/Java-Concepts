package org.example.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortbyMarksOfEmplyoees {

    public static void main(String[] args) {
    Employee e1 = new Employee(1,99,"hari");
    Employee e2 = new Employee(2,93,"hari1");

    Employee e3 = new Employee(2,39,"hari2");

    Employee e4 = new Employee(3,349,"hari3");

    ArrayList<Employee> list = new ArrayList<>();
    list.add(e1);
    list.add(e2);
    list.add(e3);
    list.add(e4);

         list.stream().sorted(Comparator.comparing(c->c.marks))
            .forEach(System.out::println);


    }


}
