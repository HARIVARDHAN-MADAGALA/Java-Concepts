package CodingINteview.TwoPointer;

public class LargeContanier {

    private static String cleanString(String sentence) {

        return null;
    }

    public static void main(String[] args) {

        int[] arr = { 2, 7, 8, 3, 7, 6 };

        int start = 0;
        int end = arr.length - 1;
        int max = 0;
        int currentSum = 0;

        while (start < end) {

            currentSum = (end - start) * Math.min(arr[start], arr[end]);
            max = Math.max(currentSum, max);

            if (arr[start] < arr[end]) {
                start++;
            }else{
            end--;
            }
        }
        System.out.println(max);
    }

}
