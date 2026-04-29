package org.example.concepts.comparator_comparable;

import java.util.Comparator;

public class Student2 {

    int id;
    String name;

    public Student2() {
    }

    public Student2(int id, String name ) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Student2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
