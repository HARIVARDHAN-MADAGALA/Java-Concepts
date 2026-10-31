package org.example.streams.exception;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;

public class New_List {

    public static void main(String[] args) {

        Employee e1 = new Employee("A", 50000);
        Employee e2 = new Employee("B", 30000);
        Employee e3 = new Employee("C", 70000);


        List<Employee> employees = Arrays.asList(e1, e2, e3);

//        employees.forEach(System.out::println);

//        List<Employee> result = employees.stream()
//                .map(e -> {
//                    e.setSalary(e.getSalary() * 1.10);
//                    return e;
//                })
//                .collect(Collectors.toList());

        List<Employee> result2 = employees.stream().map( c -> new Employee(c.getName(),c.getSalary() *1.1))
                        .collect(Collectors.toList());


        System.out.println("\nAfter salary increment:");

        System.out.println(employees);
        System.out.println(result2);
    }
}


// Employee class
class Employee {

    private String name;
    private double salary;

    // Constructor
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for salary
    public double getSalary() {
        return salary;
    }

    // Setter for salary
    public void setSalary(double salary) {
        this.salary = salary;
    }

    // toString()
    @Override
    public String toString() {
        return "Employee{name='" + name + "', salary=" + salary + "}";
    }
}
