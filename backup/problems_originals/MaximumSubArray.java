package CodingINteview.problems.sliding_window;

public class MaximumSubArray {

    public static int method(int[] arr){

        int highest = arr[0];
        int currentSum = 0;

        for(int end = 0 ; end< arr.length; end ++){

            currentSum += arr[end];

            if(currentSum > highest){
                highest = currentSum;
            }
            if(currentSum < 0){
                currentSum = 0;
            }
        }return highest;
    }

    public static void main(String[] args) {

        int[] arr = {-5, -3, -1, -2, -4};

        System.out.println(
                method(arr)
        );
    }
}




/// Kadane’s Algorithm (Maximum Subarray Sum)
/// Code (best version)

//public static int kadane(int[] arr) {
//
//    int current = arr[0];
//    int best = arr[0];
//
//    for (int i = 1; i < arr.length; i++) {
//        current = Math.max(arr[i], current + arr[i]);
//        best = Math.max(best, current);
//    }
//
//    return best;
//}
