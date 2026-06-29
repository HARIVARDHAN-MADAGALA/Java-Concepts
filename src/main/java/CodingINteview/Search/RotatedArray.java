package CodingINteview.Search;

public class RotatedArray {

    public static int rotateSearch(int[] arr, int target){

        int left = 0;
        int right = arr.length -1;

        while ( left <= right) {

            int mid = left + (right - left)/2 ;
            if( arr[mid] == target) return mid;

            if( arr[left] <= arr[mid]){

                if(arr[left] <= target && target < arr[mid]){
                    right = mid -1;
                }
                else{
                    left = mid + 1;
                }
            }else
            {
                if(arr[mid] < target && target <= arr[right]){
                    left = mid + 1;
                }
                else{
                    right = mid -1;
                }
            }

        }

            return  -1;
    }

    public static void main(String[] args) {

        int[] arr = {5,6, 7, 1, 2, 3, 4};

        int result = rotateSearch(arr, 6);

        System.out.println(result);

    }
}


/// 1. Use <= instead of <   at boundaries like left and right.
