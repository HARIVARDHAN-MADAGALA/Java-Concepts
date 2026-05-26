package org.example.concepts.comparator_comparable;

import java.util.Comparator;

public class Student implements Comparable<Student> {

    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id); // natural ordering by id
        // return this.id - other.id;
    }
}

/// Condition, Return Value, Explanation this.id<other.id,−1 (Negative), this
/// comes before other this.id=other.id,0 (Zero), They are equal
/// this.id>other.id,1 (Positive), this comes after other
