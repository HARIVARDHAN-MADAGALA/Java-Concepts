package org.example.problems.intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

///  Need to optimise

public class MergeIntervals {

    public static List<List<Integer>> mergeInterval(List<List<Integer>> intervals) {

        // Step 1: sort by start time
        intervals.sort((a, b) -> a.get(0) - b.get(0));

        List<List<Integer>> result = new ArrayList<>();

        // current interval
        List<Integer> current = intervals.get(0);

        for (int i = 1; i < intervals.size(); i++) {
            List<Integer> next = intervals.get(i);

            // overlap
            if (current.get(1) >= next.get(0)) {
                current.set(1, Math.max(current.get(1), next.get(1)));
            } else {
                // no overlap
                result.add(current);
                current = next;
            }
        }

        // add last interval
        result.add(current);

        return result;
    }


    public static void main(String[] args) {

        List<List<Integer>> list = Arrays.asList( Arrays.asList(1,3),Arrays.asList(2,6),Arrays.asList(8,10),Arrays.asList(15,18) );

        System.out.println(mergeInterval(list));
    }
}

