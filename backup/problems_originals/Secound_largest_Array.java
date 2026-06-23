package org.example.problems;

import java.sql.SQLOutput;

public class Secound_largest_Array {

    public static void main(String[] args) {

        int[] arr = {1,21,3,4,5};

        int max = Integer.MIN_VALUE;
        int max2 = Integer.MIN_VALUE;

        for(int i =0 ; i< arr.length ;i++){

            if( arr[i] > max2){
                max2 = arr[i];

                if( arr[i] > max){
                    max2 = max;
                    max = arr[i];
                }
            }
        }

        System.out.println(max2);
        System.out.println(max);
    }
}

