package org.example.problems.sliding_window;

//Find maximum sum of subarray of size K
//Example: [2,3,5,2,9,71], k = 3

import java.util.Arrays;
//Sliding window
public class MaximumSumInArrayOfSizek {

    public static int[] method(int[] num, int k){

        int currentSum = 0;
        int highestSum = 0;

        for(int i =0 ; i < k ; i++){

            currentSum+= num[i];
        }

        highestSum = currentSum;
        int startSum = 0;
        for( int i = k ; i < num.length; i++){

            currentSum += num[i] - num[i-k];

            if(currentSum > highestSum){
                highestSum = currentSum;
                startSum = i-k+1;
            }
        }

        return Arrays.copyOfRange(num,startSum,startSum+k);
    }


    public static void main(String[] args) {

        int[] num = {50, 1, 1, 1, 40};


        System.out.println(

                Arrays.toString( method( num,3))
        );
    }
}

