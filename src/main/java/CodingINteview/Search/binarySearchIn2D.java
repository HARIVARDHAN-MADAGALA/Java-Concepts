package CodingINteview.Search;

import java.util.Arrays;

/// Using Stair Case trick

public class binarySearchIn2D {

    public static int[] binarySearchIn2D(int[][] arr, int target){

        int row = 0;
        int column = arr[0].length-1;

        while( row < arr.length && column >= 0){

            int temp = arr[row][column];
            if( temp == target) return new int[]{row,column};

            if( temp > target){
                column--;
            }else{
                row++;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        int[][] matrix = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};

        int[] result = binarySearchIn2D(matrix,11);

        System.out.println(Arrays.toString(result));
    }
}
