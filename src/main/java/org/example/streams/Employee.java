package org.example.streams;

public class Employee {

    int id;
    String name;
    int marks;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", marks=" + marks +
                '}';
    }

    public Employee(int id, int marks, String name) {
        this.id = id;
        this.marks = marks;
        this.name = name;
    }


}
