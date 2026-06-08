package org.example.Conversions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayToArrayList {

    public static void main(String[] args) {

        int[] arr = {1,2,3,4,5};

        List<Integer> list = Arrays.asList(6,7,8,9,10);

        /// ArrayList to array

        int[] arr2 = list.stream().mapToInt(c -> Integer.valueOf(c)).toArray();

        /// Array to ArrayList

        List<Integer> list2 = Arrays.stream(arr).boxed().toList();

        System.out.println(

                Arrays.toString(arr2)

        );

        System.out.println(

                list2
        );
    }
}


///What actually happens at runtime?
///
/// Arrays.asList() returns:
///
/// a fixed-size list backed by the array
///
/// Internally it’s not ArrayList, it’s a private class inside java.util.Arrays
///
/// This list:
///
/// allows set() ✅
/// does NOT allow add() ❌
/// does NOT allow remove() ❌