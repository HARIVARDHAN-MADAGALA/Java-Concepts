package org.example.problems;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/// 29. Group words by their length using Java 8
/// For example:
/// input=["cloud", "aws", "azure", "gcp", "docker"]
// output={3=[aws, gcp], 5=[cloud, azure], 6=[docker]}


public class groupWordsByLength {

    public static void main(String[] args) {

        String[] input={"cloud", "aws", "azure", "gcp", "docker"};

        System.out.println(

        Arrays.stream(input).collect(Collectors.groupingBy(c->c.length(),Collectors.toList()))

        );
    }

}
