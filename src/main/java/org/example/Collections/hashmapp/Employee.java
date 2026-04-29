package org.example.Collections.hashmapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Employee {

    private int id;
    private String name;
    private int salary;

    public Employee(int id, String name ,int salary) {
        this.id = id;
        this.salary = salary;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                '}';


    }
/// IF YOU OVERRIDE ONLY EQUALS THEN IT WILL NOT WORK, U CAN COMMENT AND CHECK
    @Override
    public int hashCode() {
        // Only salary decides the hash
        return Objects.hash(salary);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        // Only salary decides equality
        return salary == other.salary;
    }

    public static void main(String[] args) {

        Employee obj1 = new Employee(1,"rahul",100);
        Employee obj2 = new Employee(1,"enam",300);
        Employee obj3 = new Employee(2,"naker",300);

        Map<Employee,Integer> map = new HashMap<>();



        map.put(obj2,11);
//        map.put(obj2,1);
//        map.put(obj3,1);



        System.out.println(map.get(obj3));
    }
}
