package CodingINteview.TwoPointer;

import java.util.Arrays;

/// Statement
/// Given a sorted integer array nums, remove the duplicates in place so each unique value appears exactly once. Return the new length k. The first k elements of nums must hold the unique values in their original sorted order. What lies past index k does not matter.
/// Inputs and outputs
/// Input. A sorted integer array nums of length n, with 0 ≤ n ≤ 3 · 10⁴ and values in [-100, 100].
/// Output. An integer k, the count of distinct values. The first k slots of nums must contain those distinct values in non-decreasing order.
/// Example. nums = [1, 1, 2, 3, 3, 4] → return 4; nums becomes [1, 2, 3, 4, _, _] where _ is "don't care".
///

public class RemoveDuplicates {

    public static int removeDuplicates(int[] arr){

        int left = 0;
        int right = 1;

        while( right < arr.length){

            if( arr[right] != arr[left]){

                left += 1;
                arr[left] = arr[right];
                right += 1;
            }else{
                right += 1;
            }
        }
        return left + 1;
    }

    public static void main(String[] args) {

       int[] nums = {1, 1, 2, 3, 3, 4 };

       int result = removeDuplicates(nums);

        System.out.println(result);

    }
}
