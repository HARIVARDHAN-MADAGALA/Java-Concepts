package org.example.problems.strings;

import java.util.StringJoiner;

public class Stringjoiner {

    public static void main(String[] args) {

        StringJoiner s = new StringJoiner(",","[","]");
        s.add("2").add("d").add("saf");

        System.out.println(s);

    }
}

