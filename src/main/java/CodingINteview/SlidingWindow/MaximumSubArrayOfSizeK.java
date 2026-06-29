package CodingINteview.SlidingWindow;

import java.util.Arrays;

public class MaximumSubArrayOfSizeK {

    public static int maximumSubArrayOfSizeK(int[] arr, int k){

        int windowSum = 0;

        for( int i = 0; i < k; i++ ) { windowSum += arr[i];}

        int max = windowSum;

        for( int i = k ; i < arr.length; i++){
            windowSum = windowSum + arr[i] - arr[i - k];
            max = Math.max(max,windowSum);
        }

        return max;
    }

    public static void main(String[] args) {

        int[] arr = {2, 1, 5, 1, 3, 2};

        int result = maximumSubArrayOfSizeK(arr,3);

        System.out.println(result);

    }
}
