package org.example.streams;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
                new Employee(1, "John", "IT", 90000),
                new Employee(2, "David", "IT", 120000),
                new Employee(3, "Mike", "IT", 100000),
                new Employee(4, "Sam", "IT", 80000),

                new Employee(5, "Alex", "HR", 70000),
                new Employee(6, "Robert", "HR", 90000),
                new Employee(7, "James", "HR", 80000),
                new Employee(8, "Tom", "HR", 60000),

                new Employee(9, "Chris", "Finance", 110000),
                new Employee(10, "Mark", "Finance", 95000),
                new Employee(11, "Steve", "Finance", 85000),
                new Employee(12, "Peter", "Finance", 75000)
        );

        Map<String, List<Employee>> result =
                employees.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDept,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparing(Employee::getSalary)
                                                        .reversed())
                                                .limit(3)
                                                .collect(Collectors.toList())
                                )
                        ));

        result.forEach((dept, empList) -> {
            System.out.println("Department: " + dept);

            empList.forEach(e ->
                    System.out.println(
                            e.getName() + " -> " + e.getSalary()
                    )
            );

            System.out.println();
        });
    }
}

class Employee {

    private int id;
    private String name;
    private String dept;
    private int salary;

    public Employee(int id, String name, String dept, int salary) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public int getSalary() {
        return salary;
    }
}