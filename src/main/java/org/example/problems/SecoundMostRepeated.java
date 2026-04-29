package org.example.problems;

import org.example.Rough.B;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/// 28. Find the 2nd most repeated character in a string.
/// For example:
/// input="aaaabbccbdd"
/// outut="b"
///
/// 29. Group words by their length using Java 8
/// For example:
/// input=["cloud", "aws", "azure", "gcp", "docker"]
/// output={3=[aws, gcp], 5=[cloud, azure], 6=[docker]}
///
/// 30. Find the first non-repeating character in a string
/// For example:
/// input="aabbccdfe"
/// output="d"
///
public class SecoundMostRepeated {

    public static void main(String[] args) {

        String word = "aaaabbccbdd";
        B obj1 = new B();

               word.chars().mapToObj(c -> (char)c).collect(Collectors.groupingBy(c->c, Collectors.counting()))
                        .entrySet().stream().sorted(())



    }
}
