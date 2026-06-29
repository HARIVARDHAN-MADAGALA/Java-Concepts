package CodingINteview.problems.arrays;

import java.util.Arrays;
import java.util.List;


/// Goal is to check which half is sorted (because in sorted array its easy wheither the value is present or not)
/// and if in that sorted half value is not there then check in another half
/// NOTE : Any one half we will have sorted sequence for sure

public class RotatedArray {

    public static int findPosition(List<Integer> num, int target){

        //find middle

        int start = 0;
        int end = num.size()-1;

        while (start <= end){

            int mid = (start + end) / 2;
            if (num.get(mid) == target) return mid;


            if( num.get(start) <= num.get(mid)){

                // Left side is sorted and find element
                if( num.get(start) <= target && target < num.get(mid)){
                    end = mid - 1;
                }else{
                    start = mid + 1;
                }
            }
            else{

                //  Right side is sorted and find element
                if(num.get(mid) < target && target <= num.get(end)){
                    start = mid + 1;
                }else{
                    end = mid - 1;
                }
            }
        }

        return -1;

    }

    public static void main(String[] args) {

        List<Integer> num = Arrays.asList(4,5,6,7,8,9,0,1,2,3);

        System.out.println(findPosition( num,  8));
    }
}

