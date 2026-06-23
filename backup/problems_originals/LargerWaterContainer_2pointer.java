package org.example.problems.two_pointer;

public class LargerWaterContainer_2pointer {

    public static int method(int[] nums) {

        if (nums == null || nums.length < 2) return 0;

        int left = 0;
        int right = nums.length - 1;
        int maxArea = 0;

        while (left < right) {

            int height = Math.min(nums[left], nums[right]);
            int width = right - left;

            maxArea = Math.max(maxArea, height * width);

            if (nums[left] < nums[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }
    public static void main(String[] args) {


        int[] nums = {1,8,6,2,5,4,8,3,7};


        System.out.println(

                method(nums)

        );
    }
}

