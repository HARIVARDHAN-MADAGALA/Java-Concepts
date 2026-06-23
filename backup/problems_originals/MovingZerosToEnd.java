package org.example.problems;

import java.util.Arrays;

public class MovingZerosToEnd {

    public static int[] method(int[] arr){

        int c= 0;
        for(int i =0; i < arr.length; i++){

            if(arr[i] != 0){
                arr[c++] = arr[i];
            }

        }

        while ( c < arr.length){

            arr[c++] = 0;
        }

        return arr;
    }

    public static void main(String[] args) {
        int[] arr = {0, 1, 0, 3, 12};

        System.out.println(

                Arrays.toString( method(arr))
        );
    }
}

