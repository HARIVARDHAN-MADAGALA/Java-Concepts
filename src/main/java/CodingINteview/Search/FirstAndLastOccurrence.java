package CodingINteview.Search;

import java.util.Arrays;

public class FirstAndLastOccurrence {

    public static int findFirst(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                ans = mid;            // record, keep going LEFT
                hi  = mid - 1;
            } else if (nums[mid] < target) {
                lo  = mid + 1;
            } else {
                hi  = mid - 1;
            }
        }
        return ans;
    }

    public static int findLast(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1, ans = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                ans = mid;            // record, keep going RIGHT
                lo  = mid + 1;
            } else if (nums[mid] < target) {
                lo  = mid + 1;
            } else {
                hi  = mid - 1;
            }
        }
        return ans;
    }

    public static int[] firstAndLastOccurrence(int[] nums, int target) {
        return new int[]{ findFirst(nums, target), findLast(nums, target) };
    }

    public static void main(String[] args) {

        int[] arr = {5, 7, 7, 8, 8, 10};

        int[] result = firstAndLastOccurrence(arr,8);

        System.out.println(Arrays.toString(result));


    }
}
