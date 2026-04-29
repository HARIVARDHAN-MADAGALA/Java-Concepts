package org.example.problems;

// get the count of subarrays whose step is decrasing by 1
// if [3,2,1,4,3]  count = 4 since [3,2,1], [3,2], [2,1], [4,3]


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubArraysCount {

    public static long method(List<Integer> list) {

        long count = 0;
        int streak = 0;

        for (int i = 1; i < list.size(); i++) {

            if (list.get(i - 1) - list.get(i) == 1) {
                streak += 1;
                count += streak;
            } else {
                streak = 0;
            }
        }

        return count;
    }


    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(3, 2, 1, 2, 1);

        System.out.println(method(list));
        System.out.println("😁😅😒😡🙁😔😂😊☹️🥺❤️👍");
    }
}


