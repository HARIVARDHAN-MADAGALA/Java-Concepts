package CodingINteview.TwoPointer;

import org.example.Collections.array.Array;

import java.util.Arrays;

public class TwoSumForSortedArray {

    public static int[] twoSum(int[] arr, int target){

        int left = 0;
        int right = arr.length -1 ;

        while ( left < right){
            int currentSum = arr[left] + arr[right];

            if( currentSum == target ) return new int[]{left,right};

            else if( currentSum > target){
                right --;
            }else{
                left++;
            }
        }

        return null;
    }

    public static void main(String[] args) {

        int[] arr = {2, 7, 11, 15};

        int[] result = twoSum(arr,9);

        System.out.println(Arrays.toString(result));

    }
}
