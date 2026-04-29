package org.example.problems;
// Given an array of integers nums and an integer target, return indices
//of the two numbers such that they add up to target.

public class Addup_to_target {

    public int method(int[] num, int target){


        for(int i=0; i< num.length;i ++){
            for(int j = i+1 ; j < num.length ; j++){

                if  ( (num[i]+num[j])==target ){
                    System.out.println(i+" "+j);
                    return i;
                }
            }
        }
        return 0;
    }

//    Alternative :
//     public int[] twoSum(int[] nums, int target) {
//    Map<Integer, Integer> numMap = new HashMap<>();
//    for (int i = 0; i < nums.length; i++) {
//        int complement = target - nums[i];
//        if (numMap.containsKey(complement)) {
//            return new int[] { numMap.get(complement), i };
//        }
//        numMap.put(nums[i], i);
//    }
//    throw new IllegalArgumentException("No two sum solution");
//}

    public static void main(String[] args) {

        Addup_to_target a = new Addup_to_target();

        int[] arr = {1,2,3,4,5};
        a.method(arr,7);
    }
}
