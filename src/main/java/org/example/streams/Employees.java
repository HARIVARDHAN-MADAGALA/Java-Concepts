package org.example.streams;

public class Employees {

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

    public Employees(int id, int marks, String name) {
        this.id = id;
        this.marks = marks;
        this.name = name;
    }

}
