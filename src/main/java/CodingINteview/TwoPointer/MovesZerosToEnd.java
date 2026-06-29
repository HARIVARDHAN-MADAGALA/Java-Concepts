package CodingINteview.TwoPointer;

import java.util.Arrays;

public class MovesZerosToEnd {

    public static int[] movesZerosToEnd(int[] arr){

        int left = 0;
        int right = 0;

        while (right < arr.length){

            if( arr[right] != 0){

                arr[left] = arr[right];
                left++;
            }
            right++;
        }

        while( left < arr.length){
            arr[left] = 0;
            left++;
        }

        return arr;
    }

    public static void main(String[] args) {

        int[] nums = {0, 1, 0, 3, 12 };

        int[] result = movesZerosToEnd(nums);

        System.out.println(Arrays.toString(result));

    }

}



///  Alternative By Srinidhi

/// public static void moveZeros(int[] nums) {
///     int write = 0;
///     for (int read = 0; read < nums.length; read++) {
///         if (nums[read] != 0) {
///             int tmp = nums[write];
///             nums[write] = nums[read];
///             nums[read] = tmp;
///             write++;
///         }
///     }
/// }
