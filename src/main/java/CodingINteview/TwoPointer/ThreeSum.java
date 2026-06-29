package CodingINteview.TwoPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// keeping one element in hold finding two sum for remaining where two sum is -firstElement

public class ThreeSum {

    public static List<List<Integer>> ThreeSum(int[] arr) {

        List<List<Integer>> result = new ArrayList<>();

        Arrays.sort(arr);

        for (int i = 0; i < arr.length - 2; i++) {

            if (i > 0 && arr[i] == arr[i - 1]) {
                continue;
            } // skip dup i

            int target = -arr[i];

            List<List<Integer>> twoSum = twoSum(arr, i + 1, arr.length - 1, target);

            if (!twoSum.isEmpty()) {
                for (List<Integer> pair : twoSum) {
                    result.add(Arrays.asList(arr[i], pair.get(0), pair.get(1)));
                }
            }
        }
        return result;
    }
        public static List<List<Integer>> twoSum (int[] arr, int left, int right, int target){

            List<List<Integer>> list = new ArrayList<>();

            while( left < right){

            int currentSum = arr[left] + arr[right];

            if(currentSum == target) {
                list.add( Arrays.asList( arr[left],arr[right] ) );
                while (left < right && arr[left] == arr[left - 1]) {left++;}       // skip dup l
                while (left < right && arr[right] == arr[right + 1]) {right--;}    // skip dup r
                left++;
                right--;
            }
            else if ( currentSum > target) {
                right --;
            } else{
                left ++;
            }
        }
            return list;
    }



        public static void main(String[] args) {

            int[] arr = {-1, 0, 1, 2, -1, -4};

            List<List<Integer>> result = ThreeSum(arr);

                System.out.println(result);
        }
}
