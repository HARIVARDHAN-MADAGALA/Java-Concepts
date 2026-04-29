package org.example.Design_patterns.builder;

/// Building a Student Object with Many Optional Fields

public class Main {

    public static void main(String[] args) {

        Student student = new Student.StringBuilder(149,"hari").build();

        Student student2 = new Student.StringBuilder(149,"hari").college("ANITS").build();

        Student student3 = new Student.StringBuilder(149,"hari").college("ANITS")
                               .age("26").department("EEE").phone("7901482428").build();


        System.out.println(student3);

    }
}
