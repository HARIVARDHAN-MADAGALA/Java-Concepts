package org.example.problems.sliding_window;

public class MaximumSubArray {

    public static int method(int[] arr){

        int highest = arr[0];
        int currentSum = 0;

        for(int end = 0 ; end< arr.length; end ++){

            currentSum += arr[end];

            if(currentSum > highest){
                highest = currentSum;
            }
            if(currentSum < 0){
                currentSum = 0;
            }
        }return highest;
    }

    public static void main(String[] args) {

        int[] arr = {-5, -3, -1, -2, -4};

        System.out.println(
                method(arr)
        );
    }
}

