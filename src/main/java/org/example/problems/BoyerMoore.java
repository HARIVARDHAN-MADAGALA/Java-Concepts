package org.example.problems;

import java.util.Arrays;
import java.util.List;

/// Find Majority Element
///
/// An element that appears more than n/2 times.
///
public class BoyerMoore {

    public static int majorityElement(List<Integer> list) {

        int candidate = 0;
        int count = 0;

        for (int num : list) {

            if (count == 0) {
                candidate = num;
            }

            if (num == candidate) {
                count++;
            } else {
                count--;
            }
        }

        return candidate;
    }

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(2,2,1,1,1,2,2);

        majorityElement(list);
    }
}
